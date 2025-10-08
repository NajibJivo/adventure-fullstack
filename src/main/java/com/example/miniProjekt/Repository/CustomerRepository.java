package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.Customer;
import com.example.miniProjekt.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Customer> findByUserRole(UserRole role);
}
