package com.example.miniProjekt.model;

import jakarta.persistence.*;

@Entity// @Entity for at fortælle spring/jpa at denne klasse præsenterer en tabel
@Table(name="products")// @Table bruges til at navngive den products i databasen
public class Product {
    @Id// @Id markerer primary key
    @GeneratedValue// gør at den automatisk generer ID'er i databasen(1,2,3,4)
    private Long id;
    //produktets navn der gemmes som VARCHAR i databasen
    private String name;
    // Produktbeskrivelse - kan være null (ikke påkrævet)
    private String description;

    // Pris - Double bruges fordi det er simplere end BigDecimal
    private Double price;

    // @Enumerated fortæller JPA hvordan enum skal gemmes
    // EnumType.STRING gemmer "T_SHIRT" som tekst (ikke som tal 0, 1, 2)
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    // Antal produkter på lager - Integer kan være null
    private Integer stockQuantity;

    // Størrelse - kun relevant for T-shirts (S, M, L, XL)
    private String size;

    // Tom constructor - PÅKRÆVET af JPA for at kunne oprette objekter
    public Product() {}

    // Constructor med parametre - gør det nemt at oprette nye produkter
    public Product(String name, String description, Double price, ProductCategory category, Integer stockQuantity) {
        // 'this.' refererer til klassens felter, parameter uden 'this.' er metodens parameter
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stockQuantity = stockQuantity;
    }

    // Getters og Setters - tillader læsning og ændring af private felter

    // Returnerer ID'et
    public Long getId() {
        return id;
    }

    // Sætter ID'et (bruges sjældent, da det auto-genereres)
    public void setId(Long id) {
        this.id = id;
    }

    // Returnerer navnet
    public String getName() {
        return name;
    }

    // Opdaterer navnet
    public void setName(String name) {
        this.name = name;
    }

    // Returnerer beskrivelsen
    public String getDescription() {
        return description;
    }

    // Opdaterer beskrivelsen
    public void setDescription(String description) {
        this.description = description;
    }

    // Returnerer prisen
    public Double getPrice() {
        return price;
    }

    // Opdaterer prisen
    public void setPrice(Double price) {
        this.price = price;
    }

    // Returnerer kategorien (T_SHIRT, SNACK eller BEVERAGE)
    public ProductCategory getCategory() {
        return category;
    }

    // Opdaterer kategorien
    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    // Returnerer lagerbeholdning
    public Integer getStockQuantity() {
        return stockQuantity;
    }

    // Opdaterer lagerbeholdning
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    // Returnerer størrelse (kun T-shirts)
    public String getSize() {
        return size;
    }

    // Opdaterer størrelse
    public void setSize(String size) {
        this.size = size;
    }
}


