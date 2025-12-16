package com.example.core.repository;

import com.example.core.entity.RechargeOrder;
import com.example.core.enums.PaymentChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RechargeOrderRepository extends JpaRepository<RechargeOrder, Long> {

    Optional<RechargeOrder> findByOrderNo(String orderNo);

    Optional<RechargeOrder> findByChannelAndProviderTransactionId(PaymentChannel channel, String providerTransactionId);
}
