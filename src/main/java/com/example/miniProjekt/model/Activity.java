package com.example.miniProjekt.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-generate primary key
    private  Long id;

    @Column(nullable=false, length=200)
    private String name;

    @Column(nullable=false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable=false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable=false)
    private Integer duration; // minutter

    @Column(name="min_age", nullable=false)
    private Integer minAge;

    @Column(name="min_height", nullable=false)
    private Integer minHeight; // cm

    @Column(name="available_from")
    private LocalDateTime availableFrom;


    @Column(name="available_to")
    private LocalDateTime availableTo;

    @Column(name="image_url", length=255)
    private String imageUrl;

    public Activity() {

    }

    public Activity(Long id, String name, String description, BigDecimal price, int duration, int minAge,
                    int minHeight, LocalDateTime availableFrom, LocalDateTime availableTo,  String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.minAge = minAge;
        this.minHeight = minHeight;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.imageUrl = imageUrl;
    }
// ny constructor udelukkende til test
    public Activity(String name, String description, int minAge, int minHeight,
                    int duration, double price) {
        this.name = name;
        this.description = description;
        this.minAge = minAge;
        this.minHeight = minHeight;
        this.duration = duration;
        this.price = BigDecimal.valueOf(price);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(Integer minHeight) {
        this.minHeight = minHeight;
    }

    public LocalDateTime getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDateTime availableFrom) {
        this.availableFrom = availableFrom;
    }

    public LocalDateTime getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(LocalDateTime availableTo) {
        this.availableTo = availableTo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
