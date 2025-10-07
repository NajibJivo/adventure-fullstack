package com.example.miniProjekt.Service;

import com.example.miniProjekt.Repository.ReservationRepository;
import com.example.miniProjekt.model.Reservation;
import com.example.miniProjekt.model.ReservationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservation> getAllReservations(){
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id){
        return reservationRepository.findById(id);
    }

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<Reservation> getReservationsForActivity(Long activityId) {
        return reservationRepository.findByActivityId(activityId);
    }

    public List<Reservation> getReservationsByType(ReservationType type) {
        return reservationRepository.findByType(type);
    }

    public List<Reservation> getUpcomingReservations() {
        return reservationRepository.findByReservationTimeAfter(LocalDateTime.now());
    }

    public List<Reservation> getReservationsBetween(LocalDateTime start, LocalDateTime end) {
        return reservationRepository.findReservationsBetween(start, end);
    }
}
