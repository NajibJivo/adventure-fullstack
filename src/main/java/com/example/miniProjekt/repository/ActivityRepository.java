package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ActivityRepository – dataadgang for Activity-entity.
 * Kun CRUD  (ingen forretningslogik).
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Page<Activity> findByNameContainingIgnoreCase(String q, Pageable pageable);

}
