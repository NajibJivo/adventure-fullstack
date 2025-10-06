package com.example.miniProjekt.Model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;
    private String email;
    private String role; // fx "Instruktør", "Admin", "Receptionist"

    // Mange instruktører kan være tilknyttet mange reservationer
    @ManyToMany(mappedBy = "employees")
    private Set<Booking> bookings = new HashSet<>();

    // Constructors
    public Employee() {}

    public Employee(String name, String phone, String email, String role) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    // Getters/setters
    public Long getId() {
        return id; }
    public void setId(Long id) {
        this.id = id; }

    public String getName() {
        return name; }
    public void setName(String name) {
        this.name = name; }

    public String getPhone() {
        return phone; }
    public void setPhone(String phone) {
        this.phone = phone; }

    public String getEmail() {
        return email; }
    public void setEmail(String email) {
        this.email = email; }

    public String getRole() {
        return role; }
    public void setRole(String role) {
        this.role = role; }

    public Set<Booking> getReservations() {
        return bookings; }
    public void setReservations(Set<Booking> bookings) {
        this.bookings = bookings; }
}