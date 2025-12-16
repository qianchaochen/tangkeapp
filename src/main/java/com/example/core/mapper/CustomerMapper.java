package com.example.core.mapper;

import com.example.core.dto.CustomerDTO;
import com.example.core.dto.AccountDTO;
import com.example.core.entity.Customer;
import com.example.core.entity.Account;

/**
 * Customer entity mapper
 */
public class CustomerMapper {

    /**
     * Convert Customer entity to CustomerDTO
     */
    public static CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setWechatOpenid(customer.getWechatOpenid());
        dto.setName(customer.getName());
        dto.setPhone(customer.getPhone());
        dto.setSource(customer.getSource());
        dto.setDistance(customer.getDistance());
        dto.setFirstVisitAt(customer.getFirstVisitAt());
        dto.setLabel(customer.getLabel());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());

        // Map account if exists
        if (customer.getAccount() != null) {
            dto.setAccount(AccountMapper.toDTO(customer.getAccount()));
        }

        return dto;
    }

    /**
     * Convert CustomerDTO to Customer entity (without relationships)
     */
    public static Customer toEntity(CustomerDTO dto) {
        if (dto == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setWechatOpenid(dto.getWechatOpenid());
        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setSource(dto.getSource());
        customer.setDistance(dto.getDistance());
        customer.setFirstVisitAt(dto.getFirstVisitAt());
        customer.setLabel(dto.getLabel());

        return customer;
    }

    /**
     * Update Customer entity from CustomerDTO
     */
    public static void updateEntityFromDTO(CustomerDTO dto, Customer customer) {
        if (dto == null || customer == null) {
            return;
        }

        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setSource(dto.getSource());
        customer.setDistance(dto.getDistance());
        customer.setFirstVisitAt(dto.getFirstVisitAt());
        customer.setLabel(dto.getLabel());
    }
}