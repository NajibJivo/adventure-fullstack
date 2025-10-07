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
public class ReservationService {

    @Autowired
    private BookingRepository bookingRepository;

    public List<Booking> getAllReservations(){
        return bookingRepository.findAll();
    }

    public Optional<Booking> getReservationById(Long id){
        return bookingRepository.findById(id);
    }

    public Booking saveReservation(Booking booking) {
        return bookingRepository.save(booking);
    }

    public void deleteReservation(Long id) {
        bookingRepository.deleteById(id);
    }

    public List<Booking> getReservationsForActivity(Long activityId) {
        return bookingRepository.findByActivityId(activityId);
    }

    public List<Booking> getReservationsByType(ReservationType type) {
        return bookingRepository.findByType(type);
    }

    public List<Booking> getUpcomingReservations() {
        return bookingRepository.findByReservationTimeAfter(LocalDateTime.now());
    }

    public List<Booking> getReservationsBetween(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findReservationsBetween(start, end);
    }
}
