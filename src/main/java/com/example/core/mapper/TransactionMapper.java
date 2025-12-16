package com.example.core.mapper;

import com.example.core.dto.ConsumptionRecordDTO;
import com.example.core.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper for Transaction entity to DTO conversions
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    /**
     * Convert Transaction to ConsumptionRecordDTO
     */
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "customerPhone", source = "customer.phone")
    @Mapping(target = "source", source = "customer.source")
    ConsumptionRecordDTO toConsumptionRecordDTO(Transaction transaction);

    /**
     * Convert list of Transactions to list of ConsumptionRecordDTOs
     */
    java.util.List<ConsumptionRecordDTO> toConsumptionRecordDTOList(java.util.List<Transaction> transactions);
}