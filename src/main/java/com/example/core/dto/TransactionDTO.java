package com.example.core.dto;

import com.example.core.enums.TransactionType;
import com.example.core.enums.ProjectType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction DTO for data transfer
 */
public class TransactionDTO {
    private Long id;
    private Long customerId;
    private Long accountId;
    private TransactionType type;
    private BigDecimal amount;
    private ProjectType projectType;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public TransactionDTO() {}

    public TransactionDTO(Long customerId, Long accountId, TransactionType type, 
                         BigDecimal amount, ProjectType projectType) {
        this.customerId = customerId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.projectType = projectType;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }

    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getAccountId() { return accountId; }

    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public TransactionType getType() { return type; }

    public void setType(TransactionType type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public ProjectType getProjectType() { return projectType; }

    public void setProjectType(ProjectType projectType) { this.projectType = projectType; }

    public String getMetadata() { return metadata; }

    public void setMetadata(String metadata) { this.metadata = metadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}