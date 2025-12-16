package com.example.core.dto;

import com.example.core.enums.TransactionType;
import com.example.core.enums.ProjectType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for consumption record responses
 */
public class ConsumptionRecordDTO {
    
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String source;
    private TransactionType type;
    private BigDecimal amount;
    private ProjectType projectType;
    private String projectTypeDisplay;
    private String metadata;
    private LocalDateTime createdAt;
    private String formattedAmount;
    private String formattedDate;

    // Constructors
    public ConsumptionRecordDTO() {}

    public ConsumptionRecordDTO(Long id, Long customerId, String customerName, String source,
                              TransactionType type, BigDecimal amount, ProjectType projectType,
                              String metadata, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.source = source;
        this.type = type;
        this.amount = amount;
        this.projectType = projectType;
        this.metadata = metadata;
        this.createdAt = createdAt;
        this.projectTypeDisplay = getProjectTypeDisplayName(projectType);
        this.formattedAmount = formatAmount(amount);
        this.formattedDate = formatDate(createdAt);
    }

    private String getProjectTypeDisplayName(ProjectType projectType) {
        switch (projectType) {
            case GENERAL: return "普通消费";
            case POND_ARTICLES: return "池塘文章";
            case DISCOUNT_CAMPAIGN: return "折扣活动";
            case PROMOTION: return "促销活动";
            case SUBSCRIPTION: return "订阅服务";
            default: return "其他";
        }
    }

    private String formatAmount(BigDecimal amount) {
        return String.format("￥%.2f", amount);
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime.toLocalDate().toString();
    }

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }

    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }

    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }

    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getSource() { return source; }

    public void setSource(String source) { this.source = source; }

    public TransactionType getType() { return type; }

    public void setType(TransactionType type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }

    public void setAmount(BigDecimal amount) { 
        this.amount = amount;
        this.formattedAmount = formatAmount(amount);
    }

    public ProjectType getProjectType() { return projectType; }

    public void setProjectType(ProjectType projectType) { 
        this.projectType = projectType;
        this.projectTypeDisplay = getProjectTypeDisplayName(projectType);
    }

    public String getProjectTypeDisplay() { return projectTypeDisplay; }

    public void setProjectTypeDisplay(String projectTypeDisplay) { this.projectTypeDisplay = projectTypeDisplay; }

    public String getMetadata() { return metadata; }

    public void setMetadata(String metadata) { this.metadata = metadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt;
        this.formattedDate = formatDate(createdAt);
    }

    public String getFormattedAmount() { return formattedAmount; }

    public void setFormattedAmount(String formattedAmount) { this.formattedAmount = formattedAmount; }

    public String getFormattedDate() { return formattedDate; }

    public void setFormattedDate(String formattedDate) { this.formattedDate = formattedDate; }
}