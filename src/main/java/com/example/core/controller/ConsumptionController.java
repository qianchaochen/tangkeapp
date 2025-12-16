package com.example.core.controller;

import com.example.core.dto.*;
import com.example.core.enums.ProjectType;
import com.example.core.enums.TransactionType;
import com.example.core.security.AuthenticationContext;
import com.example.core.service.ConsumptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller for consumption records and statistics APIs
 */
@RestController
@RequestMapping("/api/consumption")
public class ConsumptionController {

    private final ConsumptionService consumptionService;
    private final AuthenticationContext authenticationContext;

    @Autowired
    public ConsumptionController(ConsumptionService consumptionService, AuthenticationContext authenticationContext) {
        this.consumptionService = consumptionService;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Create a new consumption record
     */
    @PostMapping("/records")
    public ResponseEntity<?> createConsumptionRecord(@Valid @RequestBody CreateConsumptionRequest request) {
        try {
            // In a real system, check if user has merchant/admin role
            Long currentUserId = authenticationContext.getCurrentCustomerId();
            if (currentUserId == null) {
                return ResponseEntity.status(403).body(Map.of("error", "Authentication required"));
            }

            ConsumptionRecordDTO record = consumptionService.createConsumptionRecord(request);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get consumption records with filtering and pagination
     */
    @GetMapping("/records")
    public ResponseEntity<?> getConsumptionRecords(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) ProjectType projectType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            // Check authentication
            Long currentUserId = authenticationContext.getCurrentCustomerId();
            if (currentUserId == null) {
                return ResponseEntity.status(403).body(Map.of("error", "Authentication required"));
            }

            ConsumptionRecordFilter filter = new ConsumptionRecordFilter(customerId, type, projectType, startDate, endDate);
            filter.setPage(page);
            filter.setSize(size);
            filter.setSortBy(sortBy);
            filter.setSortDir(sortDir);

            Page<ConsumptionRecordDTO> records = consumptionService.getConsumptionRecords(filter);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get comprehensive consumption statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getConsumptionStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            // Check authentication
            Long currentUserId = authenticationContext.getCurrentCustomerId();
            if (currentUserId == null) {
                return ResponseEntity.status(403).body(Map.of("error", "Authentication required"));
            }

            ConsumptionStatsDTO stats = consumptionService.getConsumptionStatistics(startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get daily summaries for charts
     */
    @GetMapping("/stats/daily")
    public ResponseEntity<?> getDailySummaries(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<ConsumptionStatsDTO.DailySummary> summaries = consumptionService.getDailySummaries(startDate, endDate);
            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get weekly summaries for charts
     */
    @GetMapping("/stats/weekly")
    public ResponseEntity<?> getWeeklySummaries(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<ConsumptionStatsDTO.WeeklySummary> summaries = consumptionService.getWeeklySummaries(startDate, endDate);
            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get monthly summaries for charts
     */
    @GetMapping("/stats/monthly")
    public ResponseEntity<?> getMonthlySummaries(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<ConsumptionStatsDTO.MonthlySummary> summaries = consumptionService.getMonthlySummaries(startDate, endDate);
            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get source distribution for charts
     */
    @GetMapping("/stats/sources")
    public ResponseEntity<?> getSourceDistribution(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<ConsumptionStatsDTO.SourceDistribution> distribution = consumptionService.getSourceDistribution(startDate, endDate);
            return ResponseEntity.ok(distribution);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get top spenders with VIP scores
     */
    @GetMapping("/stats/top-spenders")
    public ResponseEntity<?> getTopSpenders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") Integer limit) {

        try {
            List<ConsumptionStatsDTO.TopSpender> topSpenders = consumptionService.getTopSpenders(startDate, endDate, limit);
            return ResponseEntity.ok(topSpenders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get top frequent customers with VIP scores
     */
    @GetMapping("/stats/top-frequent")
    public ResponseEntity<?> getTopFrequentCustomers(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") Integer limit) {

        try {
            List<ConsumptionStatsDTO.TopSpender> topFrequentCustomers = consumptionService.getTopFrequentCustomers(startDate, endDate, limit);
            return ResponseEntity.ok(topFrequentCustomers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get customer VIP score
     */
    @GetMapping("/stats/vip-score/{customerId}")
    public ResponseEntity<?> getCustomerVipScore(@PathVariable Long customerId) {
        try {
            Double vipScore = consumptionService.getCustomerVipScore(customerId);
            return ResponseEntity.ok(Map.of("customerId", customerId, "vipScore", vipScore));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update VIP scores for all customers (background job)
     */
    @PostMapping("/stats/update-vip-scores")
    public ResponseEntity<?> updateAllVipScores() {
        try {
            // Check if user has admin role (in a real system)
            Long currentUserId = authenticationContext.getCurrentCustomerId();
            if (currentUserId == null) {
                return ResponseEntity.status(403).body(Map.of("error", "Admin authentication required"));
            }

            consumptionService.updateAllVipScores();
            return ResponseEntity.ok(Map.of("message", "VIP scores updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get project type breakdown for charts
     */
    @GetMapping("/stats/project-types")
    public ResponseEntity<?> getProjectTypeBreakdown(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<ConsumptionStatsDTO.ProjectTypeBreakdown> breakdown = consumptionService.getProjectTypeBreakdown(startDate, endDate);
            return ResponseEntity.ok(breakdown);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get visit frequency analysis
     */
    @GetMapping("/stats/visit-frequency")
    public ResponseEntity<?> getVisitFrequencyAnalysis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            ConsumptionStatsDTO.VisitFrequencyAnalysis analysis = consumptionService.getVisitFrequencyAnalysis(startDate, endDate);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get customer transaction history
     */
    @GetMapping("/customers/{customerId}/transactions")
    public ResponseEntity<?> getCustomerTransactions(
            @PathVariable Long customerId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) ProjectType projectType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            ConsumptionRecordFilter filter = new ConsumptionRecordFilter(customerId, type, projectType, startDate, endDate);
            filter.setPage(page);
            filter.setSize(size);
            filter.setSortBy(sortBy);
            filter.setSortDir(sortDir);

            Page<ConsumptionRecordDTO> transactions = consumptionService.getCustomerTransactions(customerId, filter);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get quick dashboard stats
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getQuickStats() {
        try {
            Map<String, Object> stats = consumptionService.getQuickStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}