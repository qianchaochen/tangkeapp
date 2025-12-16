package com.example.core.repository;

import com.example.core.entity.ArticleSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Article subscription repository interface
 */
@Repository
public interface ArticleSubscriptionRepository extends JpaRepository<ArticleSubscription, Long> {

    /**
     * Find subscriptions by customer
     */
    List<ArticleSubscription> findByCustomerId(Long customerId);

    /**
     * Find subscriptions by article
     */
    List<ArticleSubscription> findByArticleId(Long articleId);

    /**
     * Find active subscriptions by customer
     */
    List<ArticleSubscription> findByCustomerIdAndIsActiveTrue(Long customerId);

    /**
     * Find subscriptions by subscription type
     */
    List<ArticleSubscription> findBySubscriptionType(String subscriptionType);

    /**
     * Find expired subscriptions
     */
    @Query("SELECT asub FROM ArticleSubscription asub WHERE asub.expiresAt < NOW()")
    List<ArticleSubscription> findExpiredSubscriptions();
}