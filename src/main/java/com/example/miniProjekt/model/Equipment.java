package com.example.miniProjekt.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "equipment_name", length = 100, nullable = false)
    private String name;

    @Column(name = "maintenance_date")
    private LocalDate maintenanceDate;

    public Equipment() {
    }

    public Equipment(Long id, String name, LocalDate maintenanceDate) {
        this.id = id;
        this.name = name;
        this.maintenanceDate = maintenanceDate;
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

    public LocalDate getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(LocalDate maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }
}
