package com.example.miniProjekt.service;

import com.example.miniProjekt.model.*;
import com.example.miniProjekt.repository.*;
import com.example.miniProjekt.web.dto.BookingRequest;
import com.example.miniProjekt.web.dto.BookingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Service test med Mockito:
 * - Tester forretningslogik isoleret (ingen database)
 * - Mocker dependencies (repositories)
 * - Fokus på happy path + fejlhåndtering
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    BookingRepository bookingRepo;

    @Mock
    ActivityRepository activityRepo;

    @Mock
    CustomerRepository customerRepo;

    @Mock
    ActivityServiceDto activityService;

    @Mock
    CustomerService customerService;

    @InjectMocks
    BookingService service;

    private Activity testActivity;
    private Customer testCustomer;
    private Booking testBooking;

    @BeforeEach
    void setup() {
        // Opret test-objekter til brug i alle tests
        testActivity = new Activity();
        testActivity.setId(1L);
        testActivity.setName("Gokart");

        testCustomer = new Customer();
        testCustomer.setId(100L);
        testCustomer.setName("Test Kunde");
        testCustomer.setEmail("test@example.com");

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setActivity(testActivity);
        testBooking.setCustomer(testCustomer);
        testBooking.setStartDateTime(LocalDateTime.now().plusDays(1));
        testBooking.setParticipants(2);
        testBooking.setBookingStatus(BookingStatus.PENDING);
    }

    @Test
    void create_validBooking_savesAndReturns() {
        // Arrange
        BookingRequest req = new BookingRequest(
                1L, 100L,
                LocalDateTime.now().plusDays(1),
                2, BookingStatus.PENDING, null
        );

        when(activityRepo.findById(1L)).thenReturn(Optional.of(testActivity));
        when(customerRepo.findById(100L)).thenReturn(Optional.of(testCustomer));
        when(bookingRepo.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });

        // Act
        BookingResponse result = service.create(req);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.activityId()).isEqualTo(1L);
        assertThat(result.customerId()).isEqualTo(100L);
        verify(bookingRepo).save(any(Booking.class));
    }

    @Test
    void create_missingActivity_throwsException() {
        // Arrange
        BookingRequest req = new BookingRequest(
                999L, 100L,
                LocalDateTime.now().plusDays(1),
                2, BookingStatus.PENDING, null
        );

        when(activityRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Activity not found");
    }

    @Test
    void create_missingCustomer_throwsException() {
        // Arrange
        BookingRequest req = new BookingRequest(
                1L, 999L,
                LocalDateTime.now().plusDays(1),
                2, BookingStatus.PENDING, null
        );

        when(activityRepo.findById(1L)).thenReturn(Optional.of(testActivity));
        when(customerRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    void create_zeroParticipants_throwsException() {
        // Arrange
        BookingRequest req = new BookingRequest(
                1L, 100L,
                LocalDateTime.now().plusDays(1),
                0, BookingStatus.PENDING, null
        );

        // Act & Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("participants must be > 0");
    }

    @Test
    void get_existingBooking_returnsResponse() {
        // Arrange
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(testBooking));

        // Act
        BookingResponse result = service.get(1L);

        // Assert
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.activityId()).isEqualTo(1L);
        assertThat(result.customerId()).isEqualTo(100L);
    }

    @Test
    void get_nonExistingBooking_throwsException() {
        // Arrange
        when(bookingRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.get(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Booking not found");
    }

    @Test
    void list_returnsAllBookings() {
        // Arrange
        Booking b1 = testBooking;
        Booking b2 = new Booking();
        b2.setId(2L);
        b2.setActivity(testActivity);
        b2.setCustomer(testCustomer);
        b2.setStartDateTime(LocalDateTime.now().plusDays(2));
        b2.setParticipants(4);
        b2.setBookingStatus(BookingStatus.CONFIRMED);

        when(bookingRepo.findAll()).thenReturn(List.of(b1, b2));

        // Act
        List<BookingResponse> result = service.list();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(2L);
    }

    @Test
    void update_existingBooking_updatesFields() {
        // Arrange
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(testBooking));
        when(activityRepo.findById(1L)).thenReturn(Optional.of(testActivity)); // TILFØJ DENNE
        when(customerRepo.findById(100L)).thenReturn(Optional.of(testCustomer)); // TILFØJ DENNE
        when(bookingRepo.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingRequest req = new BookingRequest(
                1L, 100L,
                LocalDateTime.now().plusDays(2),
                5, BookingStatus.CONFIRMED, "Ny instruktør"
        );

        // Act
        BookingResponse result = service.update(1L, req);

        // Assert
        assertThat(result.participants()).isEqualTo(5);
        assertThat(result.bookingStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(result.instructorName()).isEqualTo("Ny instruktør");
        verify(bookingRepo).save(any(Booking.class));
    }

    @Test
    void delete_existingBooking_callsRepository() {
        // Arrange
        when(bookingRepo.existsById(1L)).thenReturn(true);

        // Act
        service.delete(1L);

        // Assert
        verify(bookingRepo).deleteById(1L);
    }

    @Test
    void delete_nonExistingBooking_throwsException() {
        // Arrange
        when(bookingRepo.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Booking not found");
    }

    @Test
    void cancel_existingBooking_setsStatusToCancelled() {
        // Arrange
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepo.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        BookingResponse result = service.cancel(1L);

        // Assert
        assertThat(result.bookingStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepo).save(any(Booking.class));
    }
    @Test
    void contentLoads(){
        assertThat(true).isTrue();
    }
}