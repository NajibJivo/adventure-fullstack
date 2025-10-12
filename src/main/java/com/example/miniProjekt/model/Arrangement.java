package com.example.miniProjekt.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "arrangement")
public class Arrangement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", length = 120, nullable = false)
    private String name;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "max_participants", nullable = false)
    private Integer participants;

    public Arrangement() {
    }

    public Arrangement(String name, LocalDateTime startDateTime, Integer participants) {
        this.name = name;
        this.startDateTime = startDateTime;
        this.participants = participants;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParticipants() {
        return participants;
    }

    public void setParticipants(Integer participants) {
        this.participants = participants;
    }
}
