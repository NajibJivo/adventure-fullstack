package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByActivity_IdAndStartDatetimeBetween(Long activityId,
                                                           LocalDateTime from, LocalDateTime to);
}
