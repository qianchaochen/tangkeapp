package com.example.core.payment;

import java.util.Map;

public class PaymentInitiation {

    private String providerPrepayId;
    private String orderString;
    private Map<String, String> clientParams;

    public PaymentInitiation() {
    }

    public PaymentInitiation(String providerPrepayId, String orderString, Map<String, String> clientParams) {
        this.providerPrepayId = providerPrepayId;
        this.orderString = orderString;
        this.clientParams = clientParams;
    }

    public String getProviderPrepayId() {
        return providerPrepayId;
    }

    public void setProviderPrepayId(String providerPrepayId) {
        this.providerPrepayId = providerPrepayId;
    }

    public String getOrderString() {
        return orderString;
    }

    public void setOrderString(String orderString) {
        this.orderString = orderString;
    }

    public Map<String, String> getClientParams() {
        return clientParams;
    }

    public void setClientParams(Map<String, String> clientParams) {
        this.clientParams = clientParams;
    }
}
