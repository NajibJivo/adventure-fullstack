package com.example.miniProjekt.Repository;

import com.example.miniProjekt.model.Booking;
import com.example.miniProjekt.model.ReservationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Find reservationer for en bestemt aktivitet
    List<Booking> findByActivityId(Long activityId);

    // Find reservationer efter type
    List<Booking> findByType(ReservationType type);

    // Find reservationer for en bestemt kunde
    List<Booking> findByCustomerNameContainingIgnoreCase(String customerName);

    // Find kommende reservationer
    List<Booking> findByReservationTimeAfter(LocalDateTime dateTime);
}
