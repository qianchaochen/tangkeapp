package com.example.core.dto;

import java.math.BigDecimal;

public class DeductResponseDTO {

    private Long transactionId;
    private BigDecimal newBalance;

    public DeductResponseDTO() {
    }

    public DeductResponseDTO(Long transactionId, BigDecimal newBalance) {
        this.transactionId = transactionId;
        this.newBalance = newBalance;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(BigDecimal newBalance) {
        this.newBalance = newBalance;
    }
}
