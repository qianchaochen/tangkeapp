package com.example.core.mapper;

import com.example.core.dto.AccountDTO;
import com.example.core.entity.Account;

/**
 * Account entity mapper
 */
public class AccountMapper {

    /**
     * Convert Account entity to AccountDTO
     */
    public static AccountDTO toDTO(Account account) {
        if (account == null) {
            return null;
        }

        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setCustomerId(account.getCustomer() != null ? account.getCustomer().getId() : null);
        dto.setBalance(account.getBalance());
        dto.setTotalRecharge(account.getTotalRecharge());
        dto.setTotalSpend(account.getTotalSpend());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());

        return dto;
    }

    /**
     * Convert AccountDTO to Account entity (without relationships)
     */
    public static Account toEntity(AccountDTO dto) {
        if (dto == null) {
            return null;
        }

        Account account = new Account();
        account.setId(dto.getId());
        account.setBalance(dto.getBalance());
        account.setTotalRecharge(dto.getTotalRecharge());
        account.setTotalSpend(dto.getTotalSpend());

        return account;
    }

    /**
     * Update Account entity from AccountDTO
     */
    public static void updateEntityFromDTO(AccountDTO dto, Account account) {
        if (dto == null || account == null) {
            return;
        }

        account.setBalance(dto.getBalance());
        account.setTotalRecharge(dto.getTotalRecharge());
        account.setTotalSpend(dto.getTotalSpend());
    }
}