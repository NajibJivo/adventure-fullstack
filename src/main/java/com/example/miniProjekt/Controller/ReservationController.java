package com.example.miniProjekt.Controller;

import com.example.miniProjekt.model.Booking;
import com.example.miniProjekt.model.ReservationType;
import com.example.miniProjekt.Service.ReservationService;

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
    public List<Booking> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(booking -> ResponseEntity.ok().body(booking))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Booking createReservation(@RequestBody Booking booking) {
        return reservationService.saveReservation(booking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateReservation(@PathVariable Long id, @RequestBody Booking bookingDetails) {
        return reservationService.getReservationById(id)
                .map(booking -> {
                    booking.setCustomerName(bookingDetails.getCustomerName());
                    booking.setCustomerPhone(bookingDetails.getCustomerPhone());
                    booking.setCustomerEmail(bookingDetails.getCustomerEmail());
                    booking.setParticipantCount(bookingDetails.getParticipantCount());
                    booking.setReservationTime(bookingDetails.getReservationTime());
                    booking.setType(bookingDetails.getType());
                    booking.setNotes(bookingDetails.getNotes());
                    return ResponseEntity.ok(reservationService.saveReservation(booking));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(booking -> {
                    reservationService.deleteReservation(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Specialiserede endpoints
    @GetMapping("/activity/{activityId}")
    public List<Booking> getReservationsForActivity(@PathVariable Long activityId) {
        return reservationService.getReservationsForActivity(activityId);
    }

    @GetMapping("/type/{type}")
    public List<Booking> getReservationsByType(@PathVariable ReservationType type) {
        return reservationService.getReservationsByType(type);
    }

    @GetMapping("/upcoming")
    public List<Booking> getUpcomingReservations() {
        return reservationService.getUpcomingReservations();
    }
}
