package com.example.core.entity;

import com.example.core.enums.TransactionType;
import com.example.core.enums.ProjectType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction entity representing all financial transactions
 */
@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transaction_customer", columnList = "customer_id"),
        @Index(name = "idx_transaction_account", columnList = "account_id"),
        @Index(name = "idx_transaction_created", columnList = "created_at"),
        @Index(name = "idx_transaction_type", columnList = "type"),
        @Index(name = "idx_transaction_project_type", columnList = "project_type"),
        @Index(name = "idx_transaction_customer_created", columnList = "customer_id, created_at")
})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_type", nullable = false)
    private ProjectType projectType;

    @Column(name = "reference_no", length = 64)
    private String referenceNo;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Transaction() {}

    public Transaction(Customer customer, Account account, TransactionType type, 
                      BigDecimal amount, ProjectType projectType) {
        this.customer = customer;
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.projectType = projectType;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Customer getCustomer() { return customer; }

    public void setCustomer(Customer customer) { this.customer = customer; }

    public Account getAccount() { return account; }

    public void setAccount(Account account) { this.account = account; }

    public TransactionType getType() { return type; }

    public void setType(TransactionType type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public ProjectType getProjectType() { return projectType; }

    public void setProjectType(ProjectType projectType) { this.projectType = projectType; }

    public String getReferenceNo() { return referenceNo; }

    public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }

    public String getMetadata() { return metadata; }

    public void setMetadata(String metadata) { this.metadata = metadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}