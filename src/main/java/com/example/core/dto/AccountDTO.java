package com.example.core.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Account DTO for data transfer
 */
public class AccountDTO {
    private Long id;
    private Long customerId;
    private BigDecimal balance;
    private BigDecimal totalRecharge;
    private BigDecimal totalSpend;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public AccountDTO() {}

    public AccountDTO(Long customerId, BigDecimal balance, BigDecimal totalRecharge, BigDecimal totalSpend) {
        this.customerId = customerId;
        this.balance = balance;
        this.totalRecharge = totalRecharge;
        this.totalSpend = totalSpend;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }

    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public BigDecimal getBalance() { return balance; }

    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public BigDecimal getTotalRecharge() { return totalRecharge; }

    public void setTotalRecharge(BigDecimal totalRecharge) { this.totalRecharge = totalRecharge; }

    public BigDecimal getTotalSpend() { return totalSpend; }

    public void setTotalSpend(BigDecimal totalSpend) { this.totalSpend = totalSpend; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}