package com.example.core.dto;

import com.example.core.enums.PaymentChannel;

import java.math.BigDecimal;
import java.util.Map;

public class RechargeInitiationDTO {

    private String orderNo;
    private PaymentChannel channel;
    private BigDecimal amount;
    private BigDecimal bonusAmount;
    private Long promotionId;

    // WeChat Mini Program payment params
    private Map<String, String> wechatPayParams;

    // Alipay app pay order string
    private String alipayOrderString;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public PaymentChannel getChannel() {
        return channel;
    }

    public void setChannel(PaymentChannel channel) {
        this.channel = channel;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(BigDecimal bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public Map<String, String> getWechatPayParams() {
        return wechatPayParams;
    }

    public void setWechatPayParams(Map<String, String> wechatPayParams) {
        this.wechatPayParams = wechatPayParams;
    }

    public String getAlipayOrderString() {
        return alipayOrderString;
    }

    public void setAlipayOrderString(String alipayOrderString) {
        this.alipayOrderString = alipayOrderString;
    }
}
