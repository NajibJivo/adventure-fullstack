package com.example.miniProjekt.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable = false, length = 120)
    private String name;

    @Column(name="phone", length = 100)
    private String phone;

    @Column(name="email", nullable = false, unique = true, length = 120)
    private String email;
    @Column(name="username", unique = true, length = 50)
    private String username;

    @Column(name="password", length = 255)
    private String password;


    @Enumerated(EnumType.STRING)
    @Column(name="user_role", nullable = false)
    private UserRole userRole;

    public Customer() {
    }

    public Customer(Long id, String name, String phone, String email, UserRole userRole) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.userRole = userRole;
    }
    public Customer(String name, String phone, String email, String username, String password, UserRole userRole) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
