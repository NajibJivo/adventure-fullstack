package com.example.miniProjekt.Service;

import com.example.miniProjekt.Repository.BookingRepository;
import com.example.miniProjekt.model.Booking;
import com.example.miniProjekt.model.ReservationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    //CRUD metoder

    // Hent alle bookings
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Hent booking efter ID
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    // Gem eller opdater booking
    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    // Slet booking
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    // Find bookings for en specifik aktivitet
    public List<Booking> getBookingsForActivity(Long activityId) {
        return bookingRepository.findByActivityId(activityId);
    }

    // Find bookings efter type (PRIVATE, PUBLIC osv.)
    public List<Booking> getBookingsByType(ReservationType type) {
        return bookingRepository.findByType(type);
    }

    // Find kommende bookings
    public List<Booking> getUpcomingBookings() {
        return bookingRepository.findByReservationTimeAfter(LocalDateTime.now());
    }
}
