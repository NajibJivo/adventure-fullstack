package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Customer;
import com.example.miniProjekt.model.UserRole;
import com.example.miniProjekt.repository.CustomerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class InitDataService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void initTestUsers() {
        if (customerRepository.count() == 0) {
            // Opret admin bruger
            Customer admin = new Customer(
                    "Admin",
                    "12345678",
                    "admin@adventurexp.dk",
                    "admin",
                    passwordEncoder.encode("admin123"),
                    UserRole.OWNER
            );

            // Opret test kunde
            Customer testCustomer = new Customer(
                    "Test Kunde",
                    "87654321",
                    "kunde@test.dk",
                    "kunde",
                    passwordEncoder.encode("kunde123"),
                    UserRole.CUSTOMER
            );

            customerRepository.save(admin);
            customerRepository.save(testCustomer);

            System.out.println("✅ Test brugere oprettet:");
            System.out.println("   Admin: admin / admin123");
            System.out.println("   Kunde: kunde / kunde123");
        }
    }
}
