package com.example.core.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for consumption statistics and summaries
 */
public class ConsumptionStatsDTO {
    
    // Summary totals
    private BigDecimal totalRevenue;
    private BigDecimal totalSpend;
    private Integer totalTransactions;
    private Integer uniqueCustomers;
    private BigDecimal averageTransactionAmount;
    
    // Time-based aggregations
    private List<DailySummary> dailySummaries;
    private List<WeeklySummary> weeklySummaries;
    private List<MonthlySummary> monthlySummaries;
    
    // Source distribution
    private List<SourceDistribution> sourceDistribution;
    
    // Top performers
    private List<TopSpender> topSpenders;
    private List<TopSpender> topFrequentCustomers;
    
    // Project type breakdown
    private List<ProjectTypeBreakdown> projectTypeBreakdown;
    
    // Visit frequency analysis
    private VisitFrequencyAnalysis visitFrequency;
    
    // Inner classes for structured data
    public static class DailySummary {
        private LocalDate date;
        private BigDecimal totalRevenue;
        private BigDecimal totalSpend;
        private Integer transactionCount;
        private Integer uniqueCustomers;

        public DailySummary() {}

        public DailySummary(LocalDate date, BigDecimal totalRevenue, BigDecimal totalSpend, 
                          Integer transactionCount, Integer uniqueCustomers) {
            this.date = date;
            this.totalRevenue = totalRevenue;
            this.totalSpend = totalSpend;
            this.transactionCount = transactionCount;
            this.uniqueCustomers = uniqueCustomers;
        }

        // Getters and Setters
        public LocalDate getDate() { return date; }

        public void setDate(LocalDate date) { this.date = date; }

        public BigDecimal getTotalRevenue() { return totalRevenue; }

        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

        public BigDecimal getTotalSpend() { return totalSpend; }

        public void setTotalSpend(BigDecimal totalSpend) { this.totalSpend = totalSpend; }

        public Integer getTransactionCount() { return transactionCount; }

        public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }

        public Integer getUniqueCustomers() { return uniqueCustomers; }

        public void setUniqueCustomers(Integer uniqueCustomers) { this.uniqueCustomers = uniqueCustomers; }
    }

    public static class WeeklySummary {
        private String weekLabel;
        private BigDecimal totalRevenue;
        private BigDecimal totalSpend;
        private Integer transactionCount;
        private Integer uniqueCustomers;

        public WeeklySummary() {}

        public WeeklySummary(String weekLabel, BigDecimal totalRevenue, BigDecimal totalSpend, 
                           Integer transactionCount, Integer uniqueCustomers) {
            this.weekLabel = weekLabel;
            this.totalRevenue = totalRevenue;
            this.totalSpend = totalSpend;
            this.transactionCount = transactionCount;
            this.uniqueCustomers = uniqueCustomers;
        }

        // Getters and Setters
        public String getWeekLabel() { return weekLabel; }

        public void setWeekLabel(String weekLabel) { this.weekLabel = weekLabel; }

        public BigDecimal getTotalRevenue() { return totalRevenue; }

        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

        public BigDecimal getTotalSpend() { return totalSpend; }

        public void setTotalSpend(BigDecimal totalSpend) { this.totalSpend = totalSpend; }

        public Integer getTransactionCount() { return transactionCount; }

        public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }

        public Integer getUniqueCustomers() { return uniqueCustomers; }

        public void setUniqueCustomers(Integer uniqueCustomers) { this.uniqueCustomers = uniqueCustomers; }
    }

    public static class MonthlySummary {
        private String monthLabel;
        private BigDecimal totalRevenue;
        private BigDecimal totalSpend;
        private Integer transactionCount;
        private Integer uniqueCustomers;

        public MonthlySummary() {}

        public MonthlySummary(String monthLabel, BigDecimal totalRevenue, BigDecimal totalSpend, 
                            Integer transactionCount, Integer uniqueCustomers) {
            this.monthLabel = monthLabel;
            this.totalRevenue = totalRevenue;
            this.totalSpend = totalSpend;
            this.transactionCount = transactionCount;
            this.uniqueCustomers = uniqueCustomers;
        }

        // Getters and Setters
        public String getMonthLabel() { return monthLabel; }

        public void setMonthLabel(String monthLabel) { this.monthLabel = monthLabel; }

        public BigDecimal getTotalRevenue() { return totalRevenue; }

        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

        public BigDecimal getTotalSpend() { return totalSpend; }

        public void setTotalSpend(BigDecimal totalSpend) { this.totalSpend = totalSpend; }

        public Integer getTransactionCount() { return transactionCount; }

        public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }

        public Integer getUniqueCustomers() { return uniqueCustomers; }

        public void setUniqueCustomers(Integer uniqueCustomers) { this.uniqueCustomers = uniqueCustomers; }
    }

    public static class SourceDistribution {
        private String source;
        private Integer count;
        private BigDecimal totalAmount;
        private Double percentage;

        public SourceDistribution() {}

        public SourceDistribution(String source, Integer count, BigDecimal totalAmount, Double percentage) {
            this.source = source;
            this.count = count;
            this.totalAmount = totalAmount;
            this.percentage = percentage;
        }

        // Getters and Setters
        public String getSource() { return source; }

        public void setSource(String source) { this.source = source; }

        public Integer getCount() { return count; }

        public void setCount(Integer count) { this.count = count; }

        public BigDecimal getTotalAmount() { return totalAmount; }

        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public Double getPercentage() { return percentage; }

        public void setPercentage(Double percentage) { this.percentage = percentage; }
    }

    public static class TopSpender {
        private Long customerId;
        private String customerName;
        private String customerPhone;
        private String source;
        private BigDecimal totalAmount;
        private Integer transactionCount;
        private Double vipScore;
        private String lastTransactionDate;

        public TopSpender() {}

        public TopSpender(Long customerId, String customerName, String customerPhone, String source,
                        BigDecimal totalAmount, Integer transactionCount, Double vipScore, 
                        String lastTransactionDate) {
            this.customerId = customerId;
            this.customerName = customerName;
            this.customerPhone = customerPhone;
            this.source = source;
            this.totalAmount = totalAmount;
            this.transactionCount = transactionCount;
            this.vipScore = vipScore;
            this.lastTransactionDate = lastTransactionDate;
        }

        // Getters and Setters
        public Long getCustomerId() { return customerId; }

        public void setCustomerId(Long customerId) { this.customerId = customerId; }

        public String getCustomerName() { return customerName; }

        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getCustomerPhone() { return customerPhone; }

        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

        public String getSource() { return source; }

        public void setSource(String source) { this.source = source; }

        public BigDecimal getTotalAmount() { return totalAmount; }

        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public Integer getTransactionCount() { return transactionCount; }

        public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }

        public Double getVipScore() { return vipScore; }

        public void setVipScore(Double vipScore) { this.vipScore = vipScore; }

        public String getLastTransactionDate() { return lastTransactionDate; }

        public void setLastTransactionDate(String lastTransactionDate) { this.lastTransactionDate = lastTransactionDate; }
    }

    public static class ProjectTypeBreakdown {
        private String projectType;
        private String projectTypeDisplay;
        private Integer count;
        private BigDecimal totalAmount;
        private Double percentage;

        public ProjectTypeBreakdown() {}

        public ProjectTypeBreakdown(String projectType, Integer count, BigDecimal totalAmount, Double percentage) {
            this.projectType = projectType;
            this.projectTypeDisplay = getProjectTypeDisplayName(projectType);
            this.count = count;
            this.totalAmount = totalAmount;
            this.percentage = percentage;
        }

        private String getProjectTypeDisplayName(String projectType) {
            switch (projectType) {
                case "GENERAL": return "普通消费";
                case "POND_ARTICLES": return "池塘文章";
                case "DISCOUNT_CAMPAIGN": return "折扣活动";
                case "PROMOTION": return "促销活动";
                case "SUBSCRIPTION": return "订阅服务";
                default: return "其他";
            }
        }

        // Getters and Setters
        public String getProjectType() { return projectType; }

        public void setProjectType(String projectType) { 
            this.projectType = projectType;
            this.projectTypeDisplay = getProjectTypeDisplayName(projectType);
        }

        public String getProjectTypeDisplay() { return projectTypeDisplay; }

        public void setProjectTypeDisplay(String projectTypeDisplay) { this.projectTypeDisplay = projectTypeDisplay; }

        public Integer getCount() { return count; }

        public void setCount(Integer count) { this.count = count; }

        public BigDecimal getTotalAmount() { return totalAmount; }

        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public Double getPercentage() { return percentage; }

        public void setPercentage(Double percentage) { this.percentage = percentage; }
    }

    public static class VisitFrequencyAnalysis {
        private Integer oneTimeCustomers;
        private Integer frequentCustomers;
        private Integer regularCustomers;
        private Double averageVisitsPerCustomer;

        public VisitFrequencyAnalysis() {}

        public VisitFrequencyAnalysis(Integer oneTimeCustomers, Integer frequentCustomers, 
                                    Integer regularCustomers, Double averageVisitsPerCustomer) {
            this.oneTimeCustomers = oneTimeCustomers;
            this.frequentCustomers = frequentCustomers;
            this.regularCustomers = regularCustomers;
            this.averageVisitsPerCustomer = averageVisitsPerCustomer;
        }

        // Getters and Setters
        public Integer getOneTimeCustomers() { return oneTimeCustomers; }

        public void setOneTimeCustomers(Integer oneTimeCustomers) { this.oneTimeCustomers = oneTimeCustomers; }

        public Integer getFrequentCustomers() { return frequentCustomers; }

        public void setFrequentCustomers(Integer frequentCustomers) { this.frequentCustomers = frequentCustomers; }

        public Integer getRegularCustomers() { return regularCustomers; }

        public void setRegularCustomers(Integer regularCustomers) { this.regularCustomers = regularCustomers; }

        public Double getAverageVisitsPerCustomer() { return averageVisitsPerCustomer; }

        public void setAverageVisitsPerCustomer(Double averageVisitsPerCustomer) { this.averageVisitsPerCustomer = averageVisitsPerCustomer; }
    }

    // Constructors
    public ConsumptionStatsDTO() {}

    public ConsumptionStatsDTO(BigDecimal totalRevenue, BigDecimal totalSpend, Integer totalTransactions,
                             Integer uniqueCustomers, BigDecimal averageTransactionAmount) {
        this.totalRevenue = totalRevenue;
        this.totalSpend = totalSpend;
        this.totalTransactions = totalTransactions;
        this.uniqueCustomers = uniqueCustomers;
        this.averageTransactionAmount = averageTransactionAmount;
    }

    // Getters and Setters
    public BigDecimal getTotalRevenue() { return totalRevenue; }

    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getTotalSpend() { return totalSpend; }

    public void setTotalSpend(BigDecimal totalSpend) { this.totalSpend = totalSpend; }

    public Integer getTotalTransactions() { return totalTransactions; }

    public void setTotalTransactions(Integer totalTransactions) { this.totalTransactions = totalTransactions; }

    public Integer getUniqueCustomers() { return uniqueCustomers; }

    public void setUniqueCustomers(Integer uniqueCustomers) { this.uniqueCustomers = uniqueCustomers; }

    public BigDecimal getAverageTransactionAmount() { return averageTransactionAmount; }

    public void setAverageTransactionAmount(BigDecimal averageTransactionAmount) { this.averageTransactionAmount = averageTransactionAmount; }

    public List<DailySummary> getDailySummaries() { return dailySummaries; }

    public void setDailySummaries(List<DailySummary> dailySummaries) { this.dailySummaries = dailySummaries; }

    public List<WeeklySummary> getWeeklySummaries() { return weeklySummaries; }

    public void setWeeklySummaries(List<WeeklySummary> weeklySummaries) { this.weeklySummaries = weeklySummaries; }

    public List<MonthlySummary> getMonthlySummaries() { return monthlySummaries; }

    public void setMonthlySummaries(List<MonthlySummary> monthlySummaries) { this.monthlySummaries = monthlySummaries; }

    public List<SourceDistribution> getSourceDistribution() { return sourceDistribution; }

    public void setSourceDistribution(List<SourceDistribution> sourceDistribution) { this.sourceDistribution = sourceDistribution; }

    public List<TopSpender> getTopSpenders() { return topSpenders; }

    public void setTopSpenders(List<TopSpender> topSpenders) { this.topSpenders = topSpenders; }

    public List<TopSpender> getTopFrequentCustomers() { return topFrequentCustomers; }

    public void setTopFrequentCustomers(List<TopSpender> topFrequentCustomers) { this.topFrequentCustomers = topFrequentCustomers; }

    public List<ProjectTypeBreakdown> getProjectTypeBreakdown() { return projectTypeBreakdown; }

    public void setProjectTypeBreakdown(List<ProjectTypeBreakdown> projectTypeBreakdown) { this.projectTypeBreakdown = projectTypeBreakdown; }

    public VisitFrequencyAnalysis getVisitFrequency() { return visitFrequency; }

    public void setVisitFrequency(VisitFrequencyAnalysis visitFrequency) { this.visitFrequency = visitFrequency; }
}