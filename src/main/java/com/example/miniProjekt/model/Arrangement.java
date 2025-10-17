package com.example.miniProjekt.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "arrangement")
public class Arrangement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_arrangement_customer"))
    private Customer customer;

    @Column(name = "title", length = 120, nullable = false)
    private String title;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "notes")
    private String notes; // TEXT i DB

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants; // NULL er tilladt i ERD

    public Arrangement() {
    }

    public Arrangement(Customer customer, String title, LocalDateTime eventDate,String notes,  Integer maxParticipants) {
        this.customer = customer;
        this.title = title;
        this.eventDate = eventDate;
        this.maxParticipants = maxParticipants;
        this.notes = notes;
    }

    // getters/setters
    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setMaxParticipants(Integer participants) {
        this.maxParticipants = participants;
    }
}
