package com.example.core.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Customer entity representing a customer in the system
 */
@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_openid", columnList = "wechat_openid"),
        @Index(name = "idx_customer_phone", columnList = "phone"),
        @Index(name = "idx_customer_source", columnList = "source"),
        @Index(name = "idx_customer_first_visit", columnList = "first_visit_at")
})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wechat_openid", unique = true, nullable = false, length = 64)
    private String wechatOpenid;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "source", nullable = false, length = 50)
    private String source;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "first_visit_at", nullable = false)
    private LocalDateTime firstVisitAt;

    @Column(name = "label", length = 500)
    private String label;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Account account;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<Transaction> transactions = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<ArticleSubscription> articleSubscriptions = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<Comment> comments = new java.util.ArrayList<>();

    // Constructors
    public Customer() {}

    public Customer(String wechatOpenid, String name, String source, LocalDateTime firstVisitAt) {
        this.wechatOpenid = wechatOpenid;
        this.name = name;
        this.source = source;
        this.firstVisitAt = firstVisitAt;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getWechatOpenid() { return wechatOpenid; }

    public void setWechatOpenid(String wechatOpenid) { this.wechatOpenid = wechatOpenid; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getSource() { return source; }

    public void setSource(String source) { this.source = source; }

    public Double getDistance() { return distance; }

    public void setDistance(Double distance) { this.distance = distance; }

    public LocalDateTime getFirstVisitAt() { return firstVisitAt; }

    public void setFirstVisitAt(LocalDateTime firstVisitAt) { this.firstVisitAt = firstVisitAt; }

    public String getLabel() { return label; }

    public void setLabel(String label) { this.label = label; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Account getAccount() { return account; }

    public void setAccount(Account account) { this.account = account; }

    public java.util.List<Transaction> getTransactions() { return transactions; }

    public void setTransactions(java.util.List<Transaction> transactions) { this.transactions = transactions; }

    public java.util.List<ArticleSubscription> getArticleSubscriptions() { return articleSubscriptions; }

    public void setArticleSubscriptions(java.util.List<ArticleSubscription> articleSubscriptions) { this.articleSubscriptions = articleSubscriptions; }

    public java.util.List<Comment> getComments() { return comments; }

    public void setComments(java.util.List<Comment> comments) { this.comments = comments; }
}