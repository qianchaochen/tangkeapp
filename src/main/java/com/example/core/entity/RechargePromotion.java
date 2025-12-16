package com.example.core.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Recharge promotion entity
 */
@Entity
@Table(name = "recharge_promotions")
public class RechargePromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "recharge_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal rechargeAmount;

    @Column(name = "bonus_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal bonusAmount;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

    @Column(name = "max_usage_per_user")
    private Integer maxUsagePerUser;

    @Column(name = "total_usage_limit")
    private Integer totalUsageLimit;

    @Column(name = "current_usage", nullable = false)
    private Integer currentUsage = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public RechargePromotion() {}

    public RechargePromotion(String name, String description, BigDecimal rechargeAmount, 
                           BigDecimal bonusAmount, LocalDateTime validFrom, LocalDateTime validTo) {
        this.name = name;
        this.description = description;
        this.rechargeAmount = rechargeAmount;
        this.bonusAmount = bonusAmount;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public BigDecimal getRechargeAmount() { return rechargeAmount; }

    public void setRechargeAmount(BigDecimal rechargeAmount) { this.rechargeAmount = rechargeAmount; }

    public BigDecimal getBonusAmount() { return bonusAmount; }

    public void setBonusAmount(BigDecimal bonusAmount) { this.bonusAmount = bonusAmount; }

    public Boolean getIsActive() { return isActive; }

    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getValidFrom() { return validFrom; }

    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidTo() { return validTo; }

    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }

    public Integer getMaxUsagePerUser() { return maxUsagePerUser; }

    public void setMaxUsagePerUser(Integer maxUsagePerUser) { this.maxUsagePerUser = maxUsagePerUser; }

    public Integer getTotalUsageLimit() { return totalUsageLimit; }

    public void setTotalUsageLimit(Integer totalUsageLimit) { this.totalUsageLimit = totalUsageLimit; }

    public Integer getCurrentUsage() { return currentUsage; }

    public void setCurrentUsage(Integer currentUsage) { this.currentUsage = currentUsage; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}