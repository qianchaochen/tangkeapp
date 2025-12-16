package com.example.core.service;

import com.example.core.entity.Account;
import com.example.core.entity.Customer;
import com.example.core.entity.Transaction;
import com.example.core.enums.ProjectType;
import com.example.core.enums.TransactionType;
import com.example.core.dto.*;
import com.example.core.mapper.TransactionMapper;
import com.example.core.repository.AccountRepository;
import com.example.core.repository.CustomerRepository;
import com.example.core.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing consumption records and statistics
 */
@Service
@Transactional
public class ConsumptionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Autowired
    public ConsumptionService(TransactionRepository transactionRepository,
                            CustomerRepository customerRepository,
                            AccountRepository accountRepository,
                            TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionMapper = transactionMapper;
    }

    /**
     * Create a new consumption record
     */
    public ConsumptionRecordDTO createConsumptionRecord(CreateConsumptionRequest request) {
        // Validate customer exists
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + request.getCustomerId()));

        // Get or create customer account
        Account account = customer.getAccount();
        if (account == null) {
            account = new Account(customer);
            customer.setAccount(account);
            accountRepository.save(account);
        }

        // Create transaction
        TransactionType type = determineTransactionType(request.getAmount());
        Transaction transaction = new Transaction(customer, account, type, request.getAmount(), request.getProjectType());
        
        // Add metadata
        StringBuilder metadata = new StringBuilder();
        if (request.getMetadata() != null) {
            metadata.append("metadata:").append(request.getMetadata());
        }
        if (request.getNotes() != null) {
            if (metadata.length() > 0) metadata.append(";");
            metadata.append("notes:").append(request.getNotes());
        }
        transaction.setMetadata(metadata.toString());

        // Update account balance
        if (type == TransactionType.SPEND) {
            if (account.getBalance().compareTo(request.getAmount()) < 0) {
                throw new IllegalArgumentException("Insufficient balance for spending transaction");
            }
            account.setBalance(account.getBalance().subtract(request.getAmount()));
            account.setTotalSpend(account.getTotalSpend().add(request.getAmount()));
        } else if (type == TransactionType.RECHARGE) {
            account.setBalance(account.getBalance().add(request.getAmount()));
            account.setTotalRecharge(account.getTotalRecharge().add(request.getAmount()));
        }

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Convert to DTO
        ConsumptionRecordDTO dto = transactionMapper.toConsumptionRecordDTO(savedTransaction);
        dto.setCustomerName(customer.getName());
        dto.setCustomerPhone(customer.getPhone());
        dto.setSource(customer.getSource());

        return dto;
    }

    /**
     * Get consumption records with filtering and pagination
     */
    public Page<ConsumptionRecordDTO> getConsumptionRecords(ConsumptionRecordFilter filter) {
        // Create pageable with sorting
        Pageable pageable = Pageable.ofSize(filter.getSize())
            .withPage(filter.getPage());
        
        // Add sorting if specified
        if (filter.getSortBy() != null && filter.getSortDir() != null) {
            org.springframework.data.domain.Sort.Direction direction = "asc".equalsIgnoreCase(filter.getSortDir()) ?
                org.springframework.data.domain.Sort.Direction.ASC :
                org.springframework.data.domain.Sort.Direction.DESC;
            
            pageable = org.springframework.data.domain.PageRequest.of(
                filter.getPage(), 
                filter.getSize(),
                org.springframework.data.domain.Sort.by(direction, filter.getSortBy())
            );
        }
        
        Page<Transaction> transactions = transactionRepository.findConsumptionRecords(
            filter.getCustomerId(),
            filter.getType(),
            filter.getProjectType(),
            filter.getStartDate(),
            filter.getEndDate(),
            pageable
        );

        return transactions.map(transaction -> {
            ConsumptionRecordDTO dto = transactionMapper.toConsumptionRecordDTO(transaction);
            dto.setCustomerName(transaction.getCustomer().getName());
            dto.setCustomerPhone(transaction.getCustomer().getPhone());
            dto.setSource(transaction.getCustomer().getSource());
            return dto;
        });
    }

    /**
     * Get comprehensive consumption statistics
     */
    public ConsumptionStatsDTO getConsumptionStatistics(LocalDate startDate, LocalDate endDate) {
        // Get basic statistics
        BigDecimal totalRevenue = transactionRepository.getTotalRevenue();
        BigDecimal totalSpend = transactionRepository.getTotalSpend();
        Long totalTransactions = transactionRepository.getTotalTransactionCount();
        Long uniqueCustomers = transactionRepository.getUniqueCustomerCount();
        BigDecimal averageTransactionAmount = transactionRepository.getAverageTransactionAmount();

        ConsumptionStatsDTO stats = new ConsumptionStatsDTO(totalRevenue, totalSpend, 
            totalTransactions.intValue(), uniqueCustomers.intValue(), averageTransactionAmount);

        // Get time-based summaries
        stats.setDailySummaries(transactionRepository.getDailySummaries(startDate, endDate));
        stats.setWeeklySummaries(transactionRepository.getWeeklySummaries(startDate, endDate));
        stats.setMonthlySummaries(transactionRepository.getMonthlySummaries(startDate, endDate));

        // Get distribution data
        stats.setSourceDistribution(transactionRepository.getSourceDistribution(startDate, endDate));
        stats.setProjectTypeBreakdown(transactionRepository.getProjectTypeBreakdown(startDate, endDate, startDate, endDate));

        // Get top performers
        Pageable topSpendersPageable = Pageable.ofSize(10);
        Pageable topFrequentPageable = Pageable.ofSize(10);

        List<ConsumptionStatsDTO.TopSpender> topSpenders = transactionRepository.getTopSpenders(startDate, endDate, topSpendersPageable);
        List<ConsumptionStatsDTO.TopSpender> topFrequentCustomers = transactionRepository.getTopFrequentCustomers(startDate, endDate, topFrequentPageable);

        // Calculate VIP scores for top performers
        calculateAndSetVipScores(topSpenders, startDate, endDate);
        calculateAndSetVipScores(topFrequentCustomers, startDate, endDate);

        stats.setTopSpenders(topSpenders);
        stats.setTopFrequentCustomers(topFrequentCustomers);

        // Get visit frequency analysis
        stats.setVisitFrequency(transactionRepository.getVisitFrequencyAnalysis(startDate, endDate));

        return stats;
    }

    /**
     * Get daily summaries for charts
     */
    public List<ConsumptionStatsDTO.DailySummary> getDailySummaries(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.getDailySummaries(startDate, endDate);
    }

    /**
     * Get weekly summaries for charts
     */
    public List<ConsumptionStatsDTO.WeeklySummary> getWeeklySummaries(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.getWeeklySummaries(startDate, endDate);
    }

    /**
     * Get monthly summaries for charts
     */
    public List<ConsumptionStatsDTO.MonthlySummary> getMonthlySummaries(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.getMonthlySummaries(startDate, endDate);
    }

    /**
     * Get source distribution for charts
     */
    public List<ConsumptionStatsDTO.SourceDistribution> getSourceDistribution(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.getSourceDistribution(startDate, endDate);
    }

    /**
     * Get top spenders with VIP scores
     */
    public List<ConsumptionStatsDTO.TopSpender> getTopSpenders(LocalDate startDate, LocalDate endDate, int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        List<ConsumptionStatsDTO.TopSpender> topSpenders = transactionRepository.getTopSpenders(startDate, endDate, pageable);
        calculateAndSetVipScores(topSpenders, startDate, endDate);
        return topSpenders;
    }

    /**
     * Get top frequent customers with VIP scores
     */
    public List<ConsumptionStatsDTO.TopSpender> getTopFrequentCustomers(LocalDate startDate, LocalDate endDate, int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        List<ConsumptionStatsDTO.TopSpender> topFrequentCustomers = transactionRepository.getTopFrequentCustomers(startDate, endDate, pageable);
        calculateAndSetVipScores(topFrequentCustomers, startDate, endDate);
        return topFrequentCustomers;
    }

    /**
     * Calculate VIP score for a specific customer
     */
    public Double getCustomerVipScore(Long customerId) {
        LocalDateTime recentDate = LocalDateTime.now().minusDays(30); // Recent activity within 30 days
        Double vipScore = transactionRepository.calculateVipScore(customerId, recentDate);
        return vipScore != null ? vipScore : 0.0;
    }

    /**
     * Update VIP scores for all customers (background job)
     */
    public void updateAllVipScores() {
        LocalDateTime recentDate = LocalDateTime.now().minusDays(30);
        
        // Get all customers who have made transactions
        List<Customer> customersWithTransactions = customerRepository.findAll().stream()
            .filter(customer -> !customer.getTransactions().isEmpty())
            .collect(Collectors.toList());

        // Calculate VIP scores in batches to avoid overwhelming the database
        for (Customer customer : customersWithTransactions) {
            Double vipScore = transactionRepository.calculateVipScore(customer.getId(), recentDate);
            if (vipScore != null && vipScore > 0) {
                // Store VIP score in customer label field for now (in a real system, you'd have a separate field)
                String currentLabel = customer.getLabel();
                String vipScoreText = String.format("VIP Score: %.1f", vipScore);
                if (currentLabel != null && !currentLabel.contains("VIP Score:")) {
                    customer.setLabel(currentLabel + "; " + vipScoreText);
                } else if (currentLabel != null) {
                    // Update existing VIP score
                    String updatedLabel = currentLabel.replaceAll("VIP Score: [\\d.]+", vipScoreText);
                    customer.setLabel(updatedLabel);
                } else {
                    customer.setLabel(vipScoreText);
                }
            }
        }
        
        customerRepository.saveAll(customersWithTransactions);
    }

    /**
     * Helper method to determine transaction type based on amount and context
     */
    private TransactionType determineTransactionType(BigDecimal amount) {
        // In a real implementation, this would be determined by the business logic
        // For now, we'll assume positive amounts are SPEND transactions
        // and would need additional context to determine RECHARGE vs SPEND
        return TransactionType.SPEND;
    }

    /**
     * Calculate and set VIP scores for a list of top spenders
     */
    private void calculateAndSetVipScores(List<ConsumptionStatsDTO.TopSpender> topSpenders, 
                                        LocalDate startDate, LocalDate endDate) {
        LocalDateTime recentDate = startDate != null ? startDate.atStartOfDay() : LocalDateTime.now().minusDays(30);
        
        for (ConsumptionStatsDTO.TopSpender spender : topSpenders) {
            Double vipScore = transactionRepository.calculateVipScore(spender.getCustomerId(), recentDate);
            spender.setVipScore(vipScore != null ? vipScore : 0.0);
        }
    }

    /**
     * Get project type breakdown for charts
     */
    public List<ConsumptionStatsDTO.ProjectTypeBreakdown> getProjectTypeBreakdown(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.getProjectTypeBreakdown(startDate, endDate, startDate, endDate);
    }

    /**
     * Get visit frequency analysis
     */
    public ConsumptionStatsDTO.VisitFrequencyAnalysis getVisitFrequencyAnalysis(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.getVisitFrequencyAnalysis(startDate, endDate);
    }

    /**
     * Get transactions for a specific customer (for customer history view)
     */
    public Page<ConsumptionRecordDTO> getCustomerTransactions(Long customerId, ConsumptionRecordFilter filter) {
        // Create pageable with sorting
        Pageable pageable = Pageable.ofSize(filter.getSize())
            .withPage(filter.getPage());
        
        // Add sorting if specified
        if (filter.getSortBy() != null && filter.getSortDir() != null) {
            org.springframework.data.domain.Sort.Direction direction = "asc".equalsIgnoreCase(filter.getSortDir()) ?
                org.springframework.data.domain.Sort.Direction.ASC :
                org.springframework.data.domain.Sort.Direction.DESC;
            
            pageable = org.springframework.data.domain.PageRequest.of(
                filter.getPage(), 
                filter.getSize(),
                org.springframework.data.domain.Sort.by(direction, filter.getSortBy())
            );
        }
        
        Page<Transaction> transactions = transactionRepository.findConsumptionRecords(
            customerId,
            filter.getType(),
            filter.getProjectType(),
            filter.getStartDate(),
            filter.getEndDate(),
            pageable
        );

        return transactions.map(transaction -> {
            ConsumptionRecordDTO dto = transactionMapper.toConsumptionRecordDTO(transaction);
            dto.setCustomerName(transaction.getCustomer().getName());
            dto.setCustomerPhone(transaction.getCustomer().getPhone());
            dto.setSource(transaction.getCustomer().getSource());
            return dto;
        });
    }

    /**
     * Get quick summary stats for dashboard
     */
    public Map<String, Object> getQuickStats() {
        BigDecimal totalRevenue = transactionRepository.getTotalRevenue();
        BigDecimal totalSpend = transactionRepository.getTotalSpend();
        Long totalTransactions = transactionRepository.getTotalTransactionCount();
        Long uniqueCustomers = transactionRepository.getUniqueCustomerCount();
        
        return Map.of(
            "totalRevenue", totalRevenue,
            "totalSpend", totalSpend,
            "totalTransactions", totalTransactions,
            "uniqueCustomers", uniqueCustomers,
            "netRevenue", totalRevenue.subtract(totalSpend)
        );
    }
}