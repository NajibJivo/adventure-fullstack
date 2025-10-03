package com.example.miniProjekt.Service;

import com.example.miniProjekt.Model.Booking;
import com.example.miniProjekt.Repository.BookingRepository;
import com.example.miniProjekt.Model.Activity;
import com.example.miniProjekt.Model.ReservationType;
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
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking testBooking;
    private Activity testActivity;

    @BeforeEach
    void setUp() {
        testActivity = new Activity("Test Activity", "Description", 10, 5, 30, 100.0, false);
        testActivity.setId(1L);

        testBooking = new Booking("John Doe", "12345678", "john@example.com",
                3, LocalDateTime.now().plusDays(1), testActivity);
        testBooking.setId(1L);
        testBooking.setType(ReservationType.PRIVATE);
    }

    @Test
    void getAllReservations_ShouldReturnAllReservations() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findAll()).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getAllReservations();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerName()).isEqualTo("John Doe");
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void getReservationById_WhenExists_ShouldReturnReservation() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        // When
        Optional<Booking> result = bookingService.getReservationById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getCustomerName()).isEqualTo("John Doe");
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void getReservationById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Booking> result = bookingService.getReservationById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(bookingRepository, times(1)).findById(999L);
    }

    @Test
    void saveReservation_ShouldReturnSavedReservation() {
        // Given
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        Booking result = bookingService.saveReservation(testBooking);

        // Then
        assertThat(result.getCustomerName()).isEqualTo("John Doe");
        assertThat(result.getParticipantCount()).isEqualTo(3);
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    void deleteReservation_ShouldCallRepositoryDelete() {
        // When
        bookingService.deleteReservation(1L);

        // Then
        verify(bookingRepository, times(1)).deleteById(1L);
    }

    @Test
    void getReservationsForActivity_ShouldReturnActivityReservations() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findByActivityId(1L)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getReservationsForActivity(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActivity().getId()).isEqualTo(1L);
        verify(bookingRepository, times(1)).findByActivityId(1L);
    }

    @Test
    void getReservationsByType_ShouldReturnReservationsOfType() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findByType(ReservationType.PRIVATE)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getReservationsByType(ReservationType.PRIVATE);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(ReservationType.PRIVATE);
        verify(bookingRepository, times(1)).findByType(ReservationType.PRIVATE);
    }

    @Test
    void getUpcomingReservations_ShouldReturnFutureReservations() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findByReservationTimeAfter(any(LocalDateTime.class))).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getUpcomingReservations();

        // Then
        assertThat(result).hasSize(1);
        verify(bookingRepository, times(1)).findByReservationTimeAfter(any(LocalDateTime.class));
    }
}