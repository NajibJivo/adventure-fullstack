package com.example.miniProjekt.Controller;

import com.example.miniProjekt.model.Booking;
import com.example.miniProjekt.model.ReservationType;
import com.example.miniProjekt.Service.BookingService; // Skift til BookingService

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings") // base-path til /bookings
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    //CRUD endpoints
    // Hent alle bookings
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // Hent booking efter ID
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(booking -> ResponseEntity.ok().body(booking))
                .orElse(ResponseEntity.notFound().build());
    }

    // Opret ny booking
    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingService.saveBooking(booking);
    }

    // Opdater eksisterende booking
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking bookingDetails) {
        return bookingService.getBookingById(id)
                .map(booking -> {
                    booking.setCustomerName(bookingDetails.getCustomerName());
                    booking.setCustomerPhone(bookingDetails.getCustomerPhone());
                    booking.setCustomerEmail(bookingDetails.getCustomerEmail());
                    booking.setParticipantCount(bookingDetails.getParticipantCount());
                    booking.setReservationTime(bookingDetails.getReservationTime());
                    booking.setType(bookingDetails.getType());
                    booking.setNotes(bookingDetails.getNotes());
                    booking.setActivity(bookingDetails.getActivity());
                    return ResponseEntity.ok(bookingService.saveBooking(booking));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Slet booking
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(booking -> {
                    bookingService.deleteBooking(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Find bookings for en specifik aktivitet
    @GetMapping("/activity/{activityId}")
    public List<Booking> getBookingsForActivity(@PathVariable Long activityId) {
        return bookingService.getBookingsForActivity(activityId);
    }

    // Find bookings efter type (PRIVATE, PUBLIC osv.)
    @GetMapping("/type/{type}")
    public List<Booking> getBookingsByType(@PathVariable ReservationType type) {
        return bookingService.getBookingsByType(type);
    }

    // Find kommende bookings
    @GetMapping("/upcoming")
    public List<Booking> getUpcomingBookings() {
        return bookingService.getUpcomingBookings();
    }
}
