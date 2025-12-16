package com.example.core.repository;

import com.example.core.entity.Transaction;
import com.example.core.enums.TransactionType;
import com.example.core.enums.ProjectType;
import com.example.core.dto.ConsumptionStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Transaction repository interface
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find transactions by customer
     */
    List<Transaction> findByCustomerId(Long customerId);

    /**
     * Find transactions by account
     */
    List<Transaction> findByAccountId(Long accountId);

    /**
     * Find transactions by type
     */
    List<Transaction> findByType(TransactionType type);

    /**
     * Find transactions by project type
     */
    List<Transaction> findByProjectType(ProjectType projectType);

    /**
     * Find transactions by customer and created date range
     */
    @Query("SELECT t FROM Transaction t WHERE t.customer.id = :customerId " +
           "AND t.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY t.createdAt DESC")
    List<Transaction> findByCustomerIdAndCreatedAtBetween(@Param("customerId") Long customerId,
                                                         @Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Find transactions by amount range
     */
    @Query("SELECT t FROM Transaction t WHERE t.amount BETWEEN :minAmount AND :maxAmount")
    List<Transaction> findByAmountBetween(@Param("minAmount") BigDecimal minAmount,
                                        @Param("maxAmount") BigDecimal maxAmount);

    /**
     * Get total transaction amount by customer and type
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.customer.id = :customerId AND t.type = :type")
    BigDecimal getTotalAmountByCustomerAndType(@Param("customerId") Long customerId,
                                             @Param("type") TransactionType type);

    /**
     * Find recent transactions with pagination support
     */
    @Query("SELECT t FROM Transaction t ORDER BY t.createdAt DESC")
    List<Transaction> findRecentTransactions();

    /**
     * Find transactions with filtering and pagination for consumption records
     */
    @Query("SELECT t FROM Transaction t WHERE " +
           "(:customerId IS NULL OR t.customer.id = :customerId) AND " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:projectType IS NULL OR t.projectType = :projectType) AND " +
           "(:startDate IS NULL OR DATE(t.createdAt) >= :startDate) AND " +
           "(:endDate IS NULL OR DATE(t.createdAt) <= :endDate) " +
           "ORDER BY t.createdAt DESC")
    Page<Transaction> findConsumptionRecords(
        @Param("customerId") Long customerId,
        @Param("type") TransactionType type,
        @Param("projectType") ProjectType projectType,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable);

    /**
     * Get total revenue (recharge transactions)
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'RECHARGE'")
    BigDecimal getTotalRevenue();

    /**
     * Get total spend (spend transactions)
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'SPEND'")
    BigDecimal getTotalSpend();

    /**
     * Get total transaction count
     */
    @Query("SELECT COUNT(t) FROM Transaction t")
    Long getTotalTransactionCount();

    /**
     * Get unique customer count
     */
    @Query("SELECT COUNT(DISTINCT t.customer.id) FROM Transaction t")
    Long getUniqueCustomerCount();

    /**
     * Get average transaction amount
     */
    @Query("SELECT COALESCE(AVG(t.amount), 0) FROM Transaction t")
    BigDecimal getAverageTransactionAmount();

    /**
     * Get daily summaries for date range
     */
    @Query("SELECT new com.example.core.dto.ConsumptionStatsDTO$DailySummary(" +
           "DATE(t.createdAt), " +
           "COALESCE(SUM(CASE WHEN t.type = 'RECHARGE' THEN t.amount ELSE 0 END), 0), " +
           "COALESCE(SUM(CASE WHEN t.type = 'SPEND' THEN t.amount ELSE 0 END), 0), " +
           "COUNT(t), " +
           "COUNT(DISTINCT t.customer.id)) " +
           "FROM Transaction t " +
           "WHERE (:startDate IS NULL OR DATE(t.createdAt) >= :startDate) AND " +
           "(:endDate IS NULL OR DATE(t.createdAt) <= :endDate) " +
           "GROUP BY DATE(t.createdAt) ORDER BY DATE(t.createdAt)")
    List<ConsumptionStatsDTO.DailySummary> getDailySummaries(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    /**
     * Get weekly summaries for date range
     */
    @Query("SELECT new com.example.core.dto.ConsumptionStatsDTO$WeeklySummary(" +
           "CONCAT(YEAR(t.createdAt), '-W', WEEK(t.createdAt)), " +
           "COALESCE(SUM(CASE WHEN t.type = 'RECHARGE' THEN t.amount ELSE 0 END), 0), " +
           "COALESCE(SUM(CASE WHEN t.type = 'SPEND' THEN t.amount ELSE 0 END), 0), " +
           "COUNT(t), " +
           "COUNT(DISTINCT t.customer.id)) " +
           "FROM Transaction t " +
           "WHERE (:startDate IS NULL OR DATE(t.createdAt) >= :startDate) AND " +
           "(:endDate IS NULL OR DATE(t.createdAt) <= :endDate) " +
           "GROUP BY YEAR(t.createdAt), WEEK(t.createdAt) " +
           "ORDER BY YEAR(t.createdAt), WEEK(t.createdAt)")
    List<ConsumptionStatsDTO.WeeklySummary> getWeeklySummaries(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    /**
     * Get monthly summaries for date range
     */
    @Query("SELECT new com.example.core.dto.ConsumptionStatsDTO$MonthlySummary(" +
           "CONCAT(YEAR(t.createdAt), '-', LPAD(MONTH(t.createdAt), 2, '0')), " +
           "COALESCE(SUM(CASE WHEN t.type = 'RECHARGE' THEN t.amount ELSE 0 END), 0), " +
           "COALESCE(SUM(CASE WHEN t.type = 'SPEND' THEN t.amount ELSE 0 END), 0), " +
           "COUNT(t), " +
           "COUNT(DISTINCT t.customer.id)) " +
           "FROM Transaction t " +
           "WHERE (:startDate IS NULL OR DATE(t.createdAt) >= :startDate) AND " +
           "(:endDate IS NULL OR DATE(t.createdAt) <= :endDate) " +
           "GROUP BY YEAR(t.createdAt), MONTH(t.createdAt) " +
           "ORDER BY YEAR(t.createdAt), MONTH(t.createdAt)")
    List<ConsumptionStatsDTO.MonthlySummary> getMonthlySummaries(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    /**
     * Get source distribution
     */
    @Query("SELECT new com.example.core.dto.ConsumptionStatsDTO$SourceDistribution(" +
           "c.source, " +
           "COUNT(t), " +
           "COALESCE(SUM(t.amount), 0), " +
           "(COUNT(t) * 100.0 / (SELECT COUNT(*) FROM Transaction t2)) " +
           ") " +
           "FROM Transaction t JOIN t.customer c " +
           "WHERE (:startDate IS NULL OR DATE(t.createdAt) >= :startDate) AND " +
           "(:endDate IS NULL OR DATE(t.createdAt) <= :endDate) " +
           "GROUP BY c.source " +
           "ORDER BY COUNT(t) DESC")
    List<ConsumptionStatsDTO.SourceDistribution> getSourceDistribution(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    /**
     * Get top spenders
     */
    @Query("SELECT new com.example.core.dto.ConsumptionStatsDTO$TopSpender(" +
           "c.id, " +
           "c.name, " +
           "c.phone, " +
           "c.source, " +
           "COALESCE(SUM(t.amount), 0), " +
           "COUNT(t), " +
           "0.0, " + // vipScore will be calculated separately
           "MAX(t.createdAt).toString() " +
           ") " +
           "FROM Transaction t JOIN t.customer c " +
           "WHERE t.type = 'SPEND' AND " +
           "(:startDate IS NULL OR DATE(t.createdAt) >= :startDate) AND " +
           "(:endDate IS NULL OR DATE(t.createdAt) <= :endDate) " +
           "GROUP BY c.id, c.name, c.phone, c.source " +
           "ORDER BY SUM(t.amount) DESC")
    List<ConsumptionStatsDTO.TopSpender> getTopSpenders(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable);

    /**
     * Get top frequent customers
     */
    @Query("SELECT new com.example.core.dto.ConsumptionStatsDTO$TopSpender(" +
           "c.id, " +
           "c.name, " +
           "c.phone, " +
           "c.source, " +
           "COALESCE(SUM(t.amount), 0), " +
           "COUNT(t), " +
           "0.0, " + // vipScore will be calculated separately
           "MAX(t.createdAt).toString() " +
           ") " +
           "FROM Transaction t JOIN t.customer c " +
           "WHERE (:startDate IS NULL OR DATE(t.createdAt) >= :startDate) AND " +
           "(:endDate IS NULL OR DATE(t.createdAt) <= :endDate) " +
           "GROUP BY c.id, c.name, c.phone, c.source " +
           "ORDER BY COUNT(t) DESC")
    List<ConsumptionStatsDTO.TopSpender> getTopFrequentCustomers(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable);

    /**
     * Get project type breakdown
     */
    @Query("SELECT new com.example.core.dto.ConsumptionStatsDTO$ProjectTypeBreakdown(" +
           "t.projectType.name(), " +
           "COUNT(t), " +
           "COALESCE(SUM(t.amount), 0), " +
           "(COUNT(t) * 100.0 / (SELECT COUNT(*) FROM Transaction t2 WHERE " +
           "(:startDate2 IS NULL OR DATE(t2.createdAt) >= :startDate2) AND " +
           "(:endDate2 IS NULL OR DATE(t2.createdAt) <= :endDate2))) " +
           ") " +
           "FROM Transaction t " +
           "WHERE (:startDate IS NULL OR DATE(t.createdAt) >= :startDate) AND " +
           "(:endDate IS NULL OR DATE(t.createdAt) <= :endDate) " +
           "GROUP BY t.projectType " +
           "ORDER BY COUNT(t) DESC")
    List<ConsumptionStatsDTO.ProjectTypeBreakdown> getProjectTypeBreakdown(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("startDate2") LocalDate startDate2,
        @Param("endDate2") LocalDate endDate2);

    /**
     * Get customer visit frequency statistics
     */
    @Query("SELECT new com.example.core.dto.ConsumptionStatsDTO$VisitFrequencyAnalysis(" +
           "SUM(CASE WHEN visit_count = 1 THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN visit_count >= 10 THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN visit_count > 1 AND visit_count < 10 THEN 1 ELSE 0 END), " +
           "AVG(visit_count) " +
           ") " +
           "FROM (SELECT c.id, COUNT(t.id) as visit_count " +
           "      FROM Customer c LEFT JOIN c.transactions t " +
           "      WHERE (:startDate IS NULL OR DATE(t.createdAt) >= :startDate) AND " +
           "      (:endDate IS NULL OR DATE(t.createdAt) <= :endDate) " +
           "      GROUP BY c.id) customer_visits")
    ConsumptionStatsDTO.VisitFrequencyAnalysis getVisitFrequencyAnalysis(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    /**
     * Calculate VIP score for a customer
     */
    @Query("SELECT COALESCE(" +
           "(SUM(CASE WHEN t.type = 'SPEND' THEN t.amount ELSE 0 END) * 0.4) + " +
           "(COUNT(t) * 0.3) + " +
           "(CASE WHEN MAX(t.createdAt) >= :recentDate THEN 100 ELSE 0 END) * 0.3, " +
           "0) " +
           "FROM Transaction t WHERE t.customer.id = :customerId")
    Double calculateVipScore(@Param("customerId") Long customerId, @Param("recentDate") LocalDateTime recentDate);

    /**
     * Get transactions with customer info for reporting
     */
    @Query("SELECT t FROM Transaction t JOIN FETCH t.customer c " +
           "WHERE (:customerId IS NULL OR c.id = :customerId) AND " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:projectType IS NULL OR t.projectType = :projectType) AND " +
           "(:startDate IS NULL OR DATE(t.createdAt) >= :startDate) AND " +
           "(:endDate IS NULL OR DATE(t.createdAt) <= :endDate) " +
           "ORDER BY t.createdAt DESC")
    List<Transaction> findTransactionsWithCustomer(
        @Param("customerId") Long customerId,
        @Param("type") TransactionType type,
        @Param("projectType") ProjectType projectType,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
}