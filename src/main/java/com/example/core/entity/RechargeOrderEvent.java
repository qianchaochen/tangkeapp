package com.example.core.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Recharge order event log for auditing state transitions.
 */
@Entity
@Table(name = "recharge_order_events", indexes = {
        @Index(name = "idx_recharge_order_events_order", columnList = "recharge_order_id"),
        @Index(name = "idx_recharge_order_events_created", columnList = "created_at")
})
public class RechargeOrderEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recharge_order_id", nullable = false)
    private RechargeOrder rechargeOrder;

    @Column(name = "from_status", length = 20)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 20)
    private String toStatus;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "raw_payload", columnDefinition = "TEXT")
    private String rawPayload;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public RechargeOrderEvent() {
    }

    public RechargeOrderEvent(RechargeOrder rechargeOrder, String fromStatus, String toStatus, String message, String rawPayload) {
        this.rechargeOrder = rechargeOrder;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.message = message;
        this.rawPayload = rawPayload;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RechargeOrder getRechargeOrder() {
        return rechargeOrder;
    }

    public void setRechargeOrder(RechargeOrder rechargeOrder) {
        this.rechargeOrder = rechargeOrder;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
