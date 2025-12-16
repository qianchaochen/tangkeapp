package com.example.core.repository;

import com.example.core.entity.RechargeOrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RechargeOrderEventRepository extends JpaRepository<RechargeOrderEvent, Long> {
}
