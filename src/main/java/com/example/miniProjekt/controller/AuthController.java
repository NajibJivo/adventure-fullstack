package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.Customer;
import com.example.miniProjekt.model.UserRole;
import com.example.miniProjekt.repository.CustomerRepository;
import com.example.miniProjekt.web.dto.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Signup endpoint
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        // Valider input
        if (request.username() == null || request.username().isBlank()) {
            return ResponseEntity.badRequest().body("Brugernavn er påkrævet");
        }
        if (request.password() == null || request.password().length() < 6) {
            return ResponseEntity.badRequest().body("Password skal være mindst 6 tegn");
        }
        if (request.email() == null || request.email().isBlank()) {
            return ResponseEntity.badRequest().body("Email er påkrævet");
        }

        // Tjek om username allerede eksisterer
        if (customerRepository.existsByUsername(request.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Brugernavn er allerede taget");
        }

        // Tjek om email allerede eksisterer
        if (customerRepository.existsByEmail(request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email er allerede registreret");
        }

        // Opret ny kunde
        Customer customer = new Customer(
                request.name(),
                request.phone(),
                request.email(),
                request.username(),
                passwordEncoder.encode(request.password()),
                UserRole.CUSTOMER  // Alle nye brugere er CUSTOMER som standard
        );

        customerRepository.save(customer);

        return ResponseEntity.status(HttpStatus.CREATED).body("Bruger oprettet succesfuldt");
    }

    // Hent nuværende bruger
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        Customer customer = customerRepository.findByUsername(username)
                .orElse(null);

        if (customer == null) {
            return ResponseEntity.notFound().build();
        }

        // Tjek om bruger er OWNER (admin)
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("OWNER"));

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", customer.getId());
        userInfo.put("username", customer.getUsername());
        userInfo.put("name", customer.getName());
        userInfo.put("email", customer.getEmail());
        userInfo.put("role", customer.getUserRole().name());
        userInfo.put("isAdmin", isAdmin);

        return ResponseEntity.ok(userInfo);
    }

}
