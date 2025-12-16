package com.example.core.repository;

import com.example.core.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Admin user repository interface
 */
@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    /**
     * Find admin user by username
     */
    Optional<AdminUser> findByUsername(String username);

    /**
     * Find admin user by email
     */
    Optional<AdminUser> findByEmail(String email);

    /**
     * Find active admin users
     */
    java.util.List<AdminUser> findByIsActiveTrue();
}