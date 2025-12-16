package com.example.core.dto;

import com.example.core.enums.TransactionType;
import com.example.core.enums.ProjectType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Customer DTO for data transfer
 */
public class CustomerDTO {
    private Long id;
    private String wechatOpenid;
    private String name;
    private String phone;
    private String source;
    private Double distance;
    private LocalDateTime firstVisitAt;
    private String label;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AccountDTO account;

    // Constructors
    public CustomerDTO() {}

    public CustomerDTO(String wechatOpenid, String name, String source, LocalDateTime firstVisitAt) {
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

    public AccountDTO getAccount() { return account; }

    public void setAccount(AccountDTO account) { this.account = account; }
}