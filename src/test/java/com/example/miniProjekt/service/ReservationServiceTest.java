package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.model.Reservation;
import com.example.miniProjekt.model.ReservationType;
import com.example.miniProjekt.Repository.ReservationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation testReservation;
    private Activity testActivity;

    @BeforeEach
    void setUp() {
        testActivity = new Activity("Test Activity", "Description", 10, 5, 30, 100.0);
        testActivity.setId(1L);

        testReservation = new Reservation("John Doe", "12345678", "john@example.com",
                3, LocalDateTime.now().plusDays(1), testActivity);
        testReservation.setId(1L);
        testReservation.setType(ReservationType.PRIVATE);
    }

    @Test
    void getAllReservations_ShouldReturnAllReservations() {
        // Given
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findAll()).thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.getAllReservations();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerName()).isEqualTo("John Doe");
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void getReservationById_WhenExists_ShouldReturnReservation() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        // When
        Optional<Reservation> result = reservationService.getReservationById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getCustomerName()).isEqualTo("John Doe");
        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void getReservationById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Reservation> result = reservationService.getReservationById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(reservationRepository, times(1)).findById(999L);
    }

    @Test
    void saveReservation_ShouldReturnSavedReservation() {
        // Given
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // When
        Reservation result = reservationService.saveReservation(testReservation);

        // Then
        assertThat(result.getCustomerName()).isEqualTo("John Doe");
        assertThat(result.getParticipantCount()).isEqualTo(3);
        verify(reservationRepository, times(1)).save(testReservation);
    }

    @Test
    void deleteReservation_ShouldCallRepositoryDelete() {
        // When
        reservationService.deleteReservation(1L);

        // Then
        verify(reservationRepository, times(1)).deleteById(1L);
    }

    @Test
    void getReservationsForActivity_ShouldReturnActivityReservations() {
        // Given
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByActivityId(1L)).thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.getReservationsForActivity(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActivity().getId()).isEqualTo(1L);
        verify(reservationRepository, times(1)).findByActivityId(1L);
    }

    @Test
    void getReservationsByType_ShouldReturnReservationsOfType() {
        // Given
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByType(ReservationType.PRIVATE)).thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.getReservationsByType(ReservationType.PRIVATE);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(ReservationType.PRIVATE);
        verify(reservationRepository, times(1)).findByType(ReservationType.PRIVATE);
    }

    @Test
    void getUpcomingReservations_ShouldReturnFutureReservations() {
        // Given
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByReservationTimeAfter(any(LocalDateTime.class))).thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.getUpcomingReservations();

        // Then
        assertThat(result).hasSize(1);
        verify(reservationRepository, times(1)).findByReservationTimeAfter(any(LocalDateTime.class));
    }
}