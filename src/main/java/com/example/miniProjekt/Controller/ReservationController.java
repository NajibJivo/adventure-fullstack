package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.Reservation;
import com.example.miniProjekt.model.ReservationType;
import com.example.miniProjekt.service.ReservationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(reservation -> ResponseEntity.ok().body(reservation))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return reservationService.saveReservation(reservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservationDetails) {
        return reservationService.getReservationById(id)
                .map(reservation -> {
                    reservation.setCustomerName(reservationDetails.getCustomerName());
                    reservation.setCustomerPhone(reservationDetails.getCustomerPhone());
                    reservation.setCustomerEmail(reservationDetails.getCustomerEmail());
                    reservation.setParticipantCount(reservationDetails.getParticipantCount());
                    reservation.setReservationTime(reservationDetails.getReservationTime());
                    reservation.setType(reservationDetails.getType());
                    reservation.setNotes(reservationDetails.getNotes());
                    return ResponseEntity.ok(reservationService.saveReservation(reservation));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(reservation -> {
                    reservationService.deleteReservation(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Specialiserede endpoints
    @GetMapping("/activity/{activityId}")
    public List<Reservation> getReservationsForActivity(@PathVariable Long activityId) {
        return reservationService.getReservationsForActivity(activityId);
    }

    @GetMapping("/type/{type}")
    public List<Reservation> getReservationsByType(@PathVariable ReservationType type) {
        return reservationService.getReservationsByType(type);
    }

    @GetMapping("/upcoming")
    public List<Reservation> getUpcomingReservations() {
        return reservationService.getUpcomingReservations();
    }
}
