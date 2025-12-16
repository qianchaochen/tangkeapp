package com.example.core.mapper;

import com.example.core.dto.TransactionDTO;
import com.example.core.entity.Transaction;

/**
 * Transaction entity mapper
 */
public class TransactionMapper {

    /**
     * Convert Transaction entity to TransactionDTO
     */
    public static TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setCustomerId(transaction.getCustomer() != null ? transaction.getCustomer().getId() : null);
        dto.setAccountId(transaction.getAccount() != null ? transaction.getAccount().getId() : null);
        dto.setType(transaction.getType());
        dto.setAmount(transaction.getAmount());
        dto.setProjectType(transaction.getProjectType());
        dto.setMetadata(transaction.getMetadata());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setUpdatedAt(transaction.getUpdatedAt());

        return dto;
    }

    /**
     * Convert TransactionDTO to Transaction entity (without relationships)
     */
    public static Transaction toEntity(TransactionDTO dto) {
        if (dto == null) {
            return null;
        }

        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        transaction.setType(dto.getType());
        transaction.setAmount(dto.getAmount());
        transaction.setProjectType(dto.getProjectType());
        transaction.setMetadata(dto.getMetadata());

        return transaction;
    }

    /**
     * Update Transaction entity from TransactionDTO
     */
    public static void updateEntityFromDTO(TransactionDTO dto, Transaction transaction) {
        if (dto == null || transaction == null) {
            return;
        }

        transaction.setType(dto.getType());
        transaction.setAmount(dto.getAmount());
        transaction.setProjectType(dto.getProjectType());
        transaction.setMetadata(dto.getMetadata());
    }
}