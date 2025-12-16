package com.example.core.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Pond article entity
 */
@Entity
@Table(name = "pond_articles")
public class PondArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "author", nullable = false, length = 100)
    private String author;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "tags", length = 500)
    private String tags;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "featured_image_url", length = 1000)
    private String featuredImageUrl;

    @Column(name = "reading_time_minutes")
    private Integer readingTimeMinutes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<ArticleSubscription> articleSubscriptions = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<Comment> comments = new java.util.ArrayList<>();

    // Constructors
    public PondArticle() {}

    public PondArticle(String title, String content, String author, String category) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public String getTags() { return tags; }

    public void setTags(String tags) { this.tags = tags; }

    public Integer getViewCount() { return viewCount; }

    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Boolean getIsPublished() { return isPublished; }

    public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }

    public LocalDateTime getPublishedAt() { return publishedAt; }

    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public String getFeaturedImageUrl() { return featuredImageUrl; }

    public void setFeaturedImageUrl(String featuredImageUrl) { this.featuredImageUrl = featuredImageUrl; }

    public Integer getReadingTimeMinutes() { return readingTimeMinutes; }

    public void setReadingTimeMinutes(Integer readingTimeMinutes) { this.readingTimeMinutes = readingTimeMinutes; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public java.util.List<ArticleSubscription> getArticleSubscriptions() { return articleSubscriptions; }

    public void setArticleSubscriptions(java.util.List<ArticleSubscription> articleSubscriptions) { this.articleSubscriptions = articleSubscriptions; }

    public java.util.List<Comment> getComments() { return comments; }

    public void setComments(java.util.List<Comment> comments) { this.comments = comments; }
}