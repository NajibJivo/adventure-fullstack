package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.Arrangement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArrangementRepository extends JpaRepository<Arrangement,Long> {
    boolean existsByNameIgnoreCase(String name);
}
