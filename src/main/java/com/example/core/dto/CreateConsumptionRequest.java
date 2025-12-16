package com.example.core.dto;

import com.example.core.enums.ProjectType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for creating new consumption records
 */
public class CreateConsumptionRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotNull(message = "Project type is required")
    private ProjectType projectType;
    
    private String metadata;
    
    private String notes;

    // Constructors
    public CreateConsumptionRequest() {}

    public CreateConsumptionRequest(Long customerId, BigDecimal amount, ProjectType projectType, String metadata, String notes) {
        this.customerId = customerId;
        this.amount = amount;
        this.projectType = projectType;
        this.metadata = metadata;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getCustomerId() { return customerId; }

    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public BigDecimal getAmount() { return amount; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public ProjectType getProjectType() { return projectType; }

    public void setProjectType(ProjectType projectType) { this.projectType = projectType; }

    public String getMetadata() { return metadata; }

    public void setMetadata(String metadata) { this.metadata = metadata; }

    public String getNotes() { return notes; }

    public void setNotes(String notes) { this.notes = notes; }
}