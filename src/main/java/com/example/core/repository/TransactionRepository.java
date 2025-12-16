package com.example.core.repository;

import com.example.core.entity.Transaction;
import com.example.core.enums.TransactionType;
import com.example.core.enums.ProjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
}