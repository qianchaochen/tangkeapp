package com.example.core.repository;

import com.example.core.entity.DiscountCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Discount campaign repository interface
 */
@Repository
public interface DiscountCampaignRepository extends JpaRepository<DiscountCampaign, Long> {

    /**
     * Find active campaigns
     */
    List<DiscountCampaign> findByIsActiveTrue();

    /**
     * Find campaigns valid at specific date
     */
    @Query("SELECT dc FROM DiscountCampaign dc WHERE dc.isActive = true " +
           "AND dc.validFrom <= :date AND dc.validTo >= :date")
    List<DiscountCampaign> findActiveCampaignsAt(@Param("date") LocalDateTime date);

    /**
     * Find campaigns by discount type
     */
    List<DiscountCampaign> findByDiscountType(String discountType);
}