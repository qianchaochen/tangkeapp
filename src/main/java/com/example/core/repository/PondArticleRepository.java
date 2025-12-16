package com.example.core.repository;

import com.example.core.entity.PondArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Pond article repository interface
 */
@Repository
public interface PondArticleRepository extends JpaRepository<PondArticle, Long> {

    /**
     * Find published articles
     */
    List<PondArticle> findByIsPublishedTrue();

    /**
     * Find published articles by category
     */
    List<PondArticle> findByCategoryAndIsPublishedTrue(String category);

    /**
     * Find articles by author
     */
    List<PondArticle> findByAuthor(String author);

    /**
     * Find articles by tags containing
     */
    @Query("SELECT pa FROM PondArticle pa WHERE pa.tags LIKE %:tag%")
    List<PondArticle> findByTagsContaining(@Param("tag") String tag);

    /**
     * Find recent published articles
     */
    @Query("SELECT pa FROM PondArticle pa WHERE pa.isPublished = true ORDER BY pa.publishedAt DESC")
    List<PondArticle> findRecentPublishedArticles();

    /**
     * Find most viewed articles
     */
    @Query("SELECT pa FROM PondArticle pa WHERE pa.isPublished = true ORDER BY pa.viewCount DESC")
    List<PondArticle> findMostViewedArticles();
}