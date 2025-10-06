package com.example.miniProjekt.Controller;

import com.example.miniProjekt.Model.Booking;
import com.example.miniProjekt.Service.BookingService;
import com.example.miniProjekt.Model.ReservationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Booking> getAllReservations() {
        return bookingService.getAllReservations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getReservationById(@PathVariable Long id) {
        return bookingService.getReservationById(id)
                .map(reservation -> ResponseEntity.ok().body(reservation))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Booking createReservation(@RequestBody Booking booking) {
        return bookingService.saveReservation(booking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateReservation(@PathVariable Long id, @RequestBody Booking bookingDetails) {
        return bookingService.getReservationById(id)
                .map(reservation -> {
                    reservation.setCustomerName(bookingDetails.getCustomerName());
                    reservation.setCustomerPhone(bookingDetails.getCustomerPhone());
                    reservation.setCustomerEmail(bookingDetails.getCustomerEmail());
                    reservation.setParticipantCount(bookingDetails.getParticipantCount());
                    reservation.setReservationTime(bookingDetails.getReservationTime());
                    reservation.setType(bookingDetails.getType());
                    reservation.setNotes(bookingDetails.getNotes());
                    return ResponseEntity.ok(bookingService.saveReservation(reservation));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        return bookingService.getReservationById(id)
                .map(reservation -> {
                    bookingService.deleteReservation(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Specialiserede endpoints
    @GetMapping("/activity/{activityId}")
    public List<Booking> getReservationsForActivity(@PathVariable Long activityId) {
        return bookingService.getReservationsForActivity(activityId);
    }

    @GetMapping("/type/{type}")
    public List<Booking> getReservationsByType(@PathVariable ReservationType type) {
        return bookingService.getReservationsByType(type);
    }

    @GetMapping("/upcoming")
    public List<Booking> getUpcomingReservations() {
        return bookingService.getUpcomingReservations();
    }

    @PutMapping("/{reservationId}/assign/{employeeId}")
    public ResponseEntity<Booking> assignEmployeeToReservation(
            @PathVariable Long reservationId,
            @PathVariable Long employeeId) {
        return bookingService.assignEmployeeToReservation(reservationId, employeeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

