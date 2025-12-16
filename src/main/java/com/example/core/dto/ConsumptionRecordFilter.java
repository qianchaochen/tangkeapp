package com.example.core.dto;

import com.example.core.enums.ProjectType;
import com.example.core.enums.TransactionType;

import java.time.LocalDate;

/**
 * Request DTO for filtering consumption records
 */
public class ConsumptionRecordFilter {
    
    private Long customerId;
    private TransactionType type;
    private ProjectType projectType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "createdAt";
    private String sortDir = "desc";

    // Constructors
    public ConsumptionRecordFilter() {}

    public ConsumptionRecordFilter(Long customerId, TransactionType type, ProjectType projectType,
                                 LocalDate startDate, LocalDate endDate) {
        this.customerId = customerId;
        this.type = type;
        this.projectType = projectType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public Long getCustomerId() { return customerId; }

    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public TransactionType getType() { return type; }

    public void setType(TransactionType type) { this.type = type; }

    public ProjectType getProjectType() { return projectType; }

    public void setProjectType(ProjectType projectType) { this.projectType = projectType; }

    public LocalDate getStartDate() { return startDate; }

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getPage() { return page; }

    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }

    public void setSize(Integer size) { this.size = size; }

    public String getSortBy() { return sortBy; }

    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDir() { return sortDir; }

    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
}