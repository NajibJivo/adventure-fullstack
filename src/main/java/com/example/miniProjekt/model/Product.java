package com.example.miniProjekt.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity// @Entity for at fortælle spring/jpa at denne klasse præsenterer en tabel
@Table( // @Table bruges til at navngive den products i databasen
        name="product",
         uniqueConstraints = {
                @UniqueConstraint(name ="uq_product_name", columnNames = "name")
        }
      )
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // matcher VARCHAR(150) NOT NULL UNIQUE
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    // matcher DECIMAL(10,2) NOT NULL
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // matcher BOOLEAN NOT NULL DEFAULT TRUE
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // getters/setters
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

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}


