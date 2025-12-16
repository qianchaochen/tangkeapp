package com.example.core.repository;

import com.example.core.entity.RechargePromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Recharge promotion repository interface
 */
@Repository
public interface RechargePromotionRepository extends JpaRepository<RechargePromotion, Long> {

    /**
     * Find active promotions
     */
    List<RechargePromotion> findByIsActiveTrue();

    /**
     * Find promotions valid at specific date
     */
    @Query("SELECT rp FROM RechargePromotion rp WHERE rp.isActive = true " +
           "AND rp.validFrom <= :date AND rp.validTo >= :date")
    List<RechargePromotion> findActivePromotionsAt(@Param("date") LocalDateTime date);

    /**
     * Find promotions by recharge amount
     */
    @Query("SELECT rp FROM RechargePromotion rp WHERE rp.rechargeAmount >= :amount")
    List<RechargePromotion> findByMinRechargeAmount(@Param("amount") Double amount);
}