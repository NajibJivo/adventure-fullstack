package com.example.miniProjekt.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sale")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sale_datetime", nullable = false)
    private LocalDateTime saleDateTime;

    public Sale() {
    }

    public Sale(Long id, LocalDateTime saleDateTime) {
        this.id = id;
        this.saleDateTime = saleDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getSaleDateTime() {
        return saleDateTime;
    }

    public void setSaleDateTime(LocalDateTime saleDateTime) {
        this.saleDateTime = saleDateTime;
    }
}
