package com.example.miniProjekt.Repository;

import com.example.miniProjekt.entity.Reservation;
import com.example.miniProjekt.entity.ReservationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Find reservationer for en bestemt aktivitet
    List<Reservation> findByActivityId(Long activityId);

    // Find reservationer efter type
    List<Reservation> findByType(ReservationType type);

    // Find reservationer for en bestemt kunde
    List<Reservation> findByCustomerNameContainingIgnoreCase(String customerName);

    // Find kommende reservationer
    List<Reservation> findByReservationTimeAfter(LocalDateTime dateTime);

    // Custom query - find reservationer i et tidsrum
    @Query("SELECT r FROM Reservation r WHERE r.reservationTime BETWEEN :start AND :end ORDER BY r.reservationTime")
    List<Reservation> findReservationsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
