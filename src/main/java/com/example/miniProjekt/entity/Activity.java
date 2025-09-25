package com.example.miniProjekt.entity;
import jakarta.persistence.*;
import java.util.List;
@Entity
@Table(name="activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;
    private String description;

    @Column(name="min_age")
    private Integer minAge;

    @Column(name="max_participants")
    private Integer maxParticipants;

    @Column(name="duration_minutes")
    private Integer durationMinutes;

    private Double price;

    @Column(name="equipment_required")
    private Boolean equipmentRequired=false;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    public Activity() {}

    public Activity(String name, String description, Integer minAge, Integer maxParticipants, Integer durationMinutes, Double price, Boolean equipmentRequired) {
        this.name = name;
        this.description = description;
        this.minAge = minAge;
        this.maxParticipants = maxParticipants;
        this.price = price;
        this.equipmentRequired = equipmentRequired;
        this.durationMinutes = durationMinutes;
    }

    // Getters og Setters (samme som før)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMinAge() { return minAge; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Boolean getEquipmentRequired() { return equipmentRequired; }
    public void setEquipmentRequired(Boolean equipmentRequired) { this.equipmentRequired = equipmentRequired; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }
}
