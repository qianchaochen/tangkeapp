package com.example.core.repository;

import com.example.core.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Customer repository interface
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find customer by WeChat OpenID
     */
    Optional<Customer> findByWechatOpenid(String wechatOpenid);

    /**
     * Find customer by phone
     */
    Optional<Customer> findByPhone(String phone);

    /**
     * Find customers by source
     */
    List<Customer> findBySource(String source);

    /**
     * Find customers by first visit date range
     */
    @Query("SELECT c FROM Customer c WHERE c.firstVisitAt BETWEEN :startDate AND :endDate")
    List<Customer> findByFirstVisitAtBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    /**
     * Find customers with label containing text
     */
    @Query("SELECT c FROM Customer c WHERE c.label LIKE %:labelText%")
    List<Customer> findByLabelContaining(@Param("labelText") String labelText);
}