package com.example.miniProjekt.Service;

import com.example.miniProjekt.Repository.ReservationRepository;
import com.example.miniProjekt.entity.Reservation;
import com.example.miniProjekt.entity.ReservationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    public List getAllReservations(){
        return reservationRepository.findAll();
    }
    public Optional getReservationById(Long id){
        return reservationRepository.findById(id);
    }
    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public List getReservationsForActivity(Long activityId) {
        return reservationRepository.findByActivityId(activityId);
    }

    public List getReservationsByType(ReservationType type) {
        return reservationRepository.findByType(type);
    }

    public List getUpcomingReservations() {
        return reservationRepository.findByReservationTimeAfter(LocalDateTime.now());
    }

    public List getReservationsBetween(LocalDateTime start, LocalDateTime end) {
        return reservationRepository.findReservationsBetween(start, end);
    }
}

