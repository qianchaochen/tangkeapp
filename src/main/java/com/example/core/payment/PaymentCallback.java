package com.example.core.payment;

public class PaymentCallback {

    private String orderNo;
    private String providerTransactionId;
    private boolean paid;
    private String rawPayload;

    public PaymentCallback() {
    }

    public PaymentCallback(String orderNo, String providerTransactionId, boolean paid, String rawPayload) {
        this.orderNo = orderNo;
        this.providerTransactionId = providerTransactionId;
        this.paid = paid;
        this.rawPayload = rawPayload;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getProviderTransactionId() {
        return providerTransactionId;
    }

    public void setProviderTransactionId(String providerTransactionId) {
        this.providerTransactionId = providerTransactionId;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }
}
