package com.example.core.repository;

import com.example.core.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Comment repository interface
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Find comments by customer
     */
    List<Comment> findByCustomerId(Long customerId);

    /**
     * Find comments by article
     */
    List<Comment> findByArticleId(Long articleId);

    /**
     * Find approved comments by article
     */
    List<Comment> findByArticleIdAndIsApprovedTrue(Long articleId);

    /**
     * Find comments by parent (nested comments)
     */
    List<Comment> findByParentId(Long parentId);

    /**
     * Find comments with high like count
     */
    @Query("SELECT c FROM Comment c WHERE c.likeCount > :likeCount ORDER BY c.likeCount DESC")
    List<Comment> findHighLikedComments(@Param("likeCount") Integer likeCount);
}