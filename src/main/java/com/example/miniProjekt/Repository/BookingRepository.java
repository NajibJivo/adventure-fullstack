package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * BookingRepository – dataadgang for Booking-entity.
 * Ingen forretningslogik her; kun CRUD og afledte forespørgsler.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}
