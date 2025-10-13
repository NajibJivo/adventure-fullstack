package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    boolean existsByNameIgnoreCase(String name);

}
