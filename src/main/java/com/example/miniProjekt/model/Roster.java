package com.example.miniProjekt.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name ="roster")
public class Roster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instructor_name", nullable = false, length = 100)
    private String instructorName;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    // Valgfrit i ERD'en, men feltet findes: FK til Customer
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Roster() {
    }

    public Roster(String instructorName, LocalDate workDate) {
        this.instructorName = instructorName;
        this.workDate = workDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
