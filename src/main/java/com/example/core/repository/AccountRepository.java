package com.example.core.repository;

import com.example.core.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Account repository interface
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Find account by customer
     */
    Optional<Account> findByCustomerId(Long customerId);

    /**
     * Find accounts with balance greater than specified amount
     */
    List<Account> findByBalanceGreaterThan(BigDecimal amount);

    /**
     * Find accounts with balance less than specified amount
     */
    List<Account> findByBalanceLessThan(BigDecimal amount);

    /**
     * Find accounts with total spend greater than specified amount
     */
    @Query("SELECT a FROM Account a WHERE a.totalSpend > :amount")
    List<Account> findByTotalSpendGreaterThan(@Param("amount") BigDecimal amount);

    /**
     * Find accounts with total recharge greater than specified amount
     */
    @Query("SELECT a FROM Account a WHERE a.totalRecharge > :amount")
    List<Account> findByTotalRechargeGreaterThan(@Param("amount") BigDecimal amount);
}