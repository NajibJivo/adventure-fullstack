package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Repository test for Booking - tester database-operationer
 */
@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepo;

    @Autowired
    ActivityRepository activityRepo;

    @Autowired
    CustomerRepository customerRepo;

    @Autowired
    TestEntityManager em;

    private Activity testActivity;
    private Customer testCustomer;

    @BeforeEach
    void setup() {
        // Opret test-aktivitet
        testActivity = new Activity();
        testActivity.setName("Test Aktivitet");
        testActivity.setDescription("Test");
        testActivity.setPrice(new BigDecimal("199"));
        testActivity.setDuration(60);
        testActivity.setMinAge(10);
        testActivity.setMinHeight(120);
        testActivity = activityRepo.save(testActivity);

        // Opret test-kunde
        testCustomer = new Customer();
        testCustomer.setName("Test Kunde");
        testCustomer.setEmail("test@example.com");
        testCustomer.setPhone("12345678");
        testCustomer.setUserRole(UserRole.CUSTOMER);
        testCustomer = customerRepo.save(testCustomer);
    }

    @Test
    void saveAndFindById_works() {
        // Arrange
        Booking booking = createBooking(
                LocalDateTime.now().plusDays(1),
                2,
                BookingStatus.PENDING
        );

        // Act
        Booking saved = bookingRepo.save(booking);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(bookingRepo.findById(saved.getId())).isPresent();
    }

    @Test
    void findByStartDateTimeBetween_returnsBookingsInRange() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2025, 6, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 6, 30, 23, 59);

        Booking b1 = createBooking(
                LocalDateTime.of(2025, 6, 10, 14, 0),
                2, BookingStatus.CONFIRMED
        );
        Booking b2 = createBooking(
                LocalDateTime.of(2025, 6, 20, 16, 0),
                4, BookingStatus.PENDING
        );
        Booking b3 = createBooking(
                LocalDateTime.of(2025, 7, 5, 10, 0), // udenfor range
                3, BookingStatus.CONFIRMED
        );

        bookingRepo.saveAll(List.of(b1, b2, b3));

        // Act
        List<Booking> result = bookingRepo.findByStartDateTimeBetween(start, end);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Booking::getId)
                .containsExactlyInAnyOrder(b1.getId(), b2.getId());
    }

    @Test
    void findByActivity_Id_returnsBookingsForActivity() {
        // Arrange
        Activity otherActivity = new Activity();
        otherActivity.setName("Anden aktivitet");
        otherActivity.setDescription("Test");
        otherActivity.setPrice(new BigDecimal("299"));
        otherActivity.setDuration(30);
        otherActivity.setMinAge(12);
        otherActivity.setMinHeight(150);
        otherActivity = activityRepo.save(otherActivity);

        Booking b1 = createBooking(
                LocalDateTime.now().plusDays(1), 2, BookingStatus.PENDING
        );
        Booking b2 = createBooking(
                LocalDateTime.now().plusDays(2), 4, BookingStatus.CONFIRMED
        );

        Booking b3 = new Booking();
        b3.setActivity(otherActivity); // anden aktivitet
        b3.setCustomer(testCustomer);
        b3.setStartDateTime(LocalDateTime.now().plusDays(3));
        b3.setParticipants(3);
        b3.setBookingStatus(BookingStatus.PENDING);

        bookingRepo.saveAll(List.of(b1, b2, b3));

        // Act
        List<Booking> result = bookingRepo.findByActivity_Id(testActivity.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(b ->
                b.getActivity().getId().equals(testActivity.getId())
        );
    }

    @Test
    void findByCustomer_Id_returnsBookingsForCustomer() {
        // Arrange
        Customer otherCustomer = new Customer();
        otherCustomer.setName("Anden kunde");
        otherCustomer.setEmail("anden@example.com");
        otherCustomer.setUserRole(UserRole.CUSTOMER);
        otherCustomer = customerRepo.save(otherCustomer);

        Booking b1 = createBooking(
                LocalDateTime.now().plusDays(1), 2, BookingStatus.PENDING
        );
        Booking b2 = createBooking(
                LocalDateTime.now().plusDays(2), 3, BookingStatus.CONFIRMED
        );

        Booking b3 = new Booking();
        b3.setActivity(testActivity);
        b3.setCustomer(otherCustomer); // anden kunde
        b3.setStartDateTime(LocalDateTime.now().plusDays(3));
        b3.setParticipants(4);
        b3.setBookingStatus(BookingStatus.PENDING);

        bookingRepo.saveAll(List.of(b1, b2, b3));

        // Act
        List<Booking> result = bookingRepo.findByCustomer_Id(testCustomer.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(b ->
                b.getCustomer().getId().equals(testCustomer.getId())
        );
    }

    @Test
    void findByBookingStatus_returnsMatchingBookings() {
        // Arrange
        Booking b1 = createBooking(
                LocalDateTime.now().plusDays(1), 2, BookingStatus.CONFIRMED
        );
        Booking b2 = createBooking(
                LocalDateTime.now().plusDays(2), 3, BookingStatus.CONFIRMED
        );
        Booking b3 = createBooking(
                LocalDateTime.now().plusDays(3), 4, BookingStatus.PENDING
        );

        bookingRepo.saveAll(List.of(b1, b2, b3));

        // Act
        List<Booking> confirmed = bookingRepo.findByBookingStatus(BookingStatus.CONFIRMED);
        List<Booking> pending = bookingRepo.findByBookingStatus(BookingStatus.PENDING);

        // Assert
        assertThat(confirmed).hasSize(2);
        assertThat(pending).hasSize(1);
        assertThat(confirmed).allMatch(b ->
                b.getBookingStatus() == BookingStatus.CONFIRMED
        );
    }

    @Test
    void deleteBooking_removesFromDatabase() {
        // Arrange
        Booking booking = createBooking(
                LocalDateTime.now().plusDays(1), 2, BookingStatus.PENDING
        );
        Booking saved = bookingRepo.save(booking);
        Long id = saved.getId();

        // Act
        bookingRepo.deleteById(id);
        em.flush(); // tvinger database-operation

        // Assert
        assertThat(bookingRepo.findById(id)).isEmpty();
    }

    @Test
    void updateBooking_changesFields() {
        // Arrange
        Booking booking = createBooking(
                LocalDateTime.now().plusDays(1), 2, BookingStatus.PENDING
        );
        Booking saved = bookingRepo.save(booking);

        // Act
        saved.setParticipants(5);
        saved.setBookingStatus(BookingStatus.CONFIRMED);
        saved.setInstructorName("Ny Instruktør");
        Booking updated = bookingRepo.save(saved);

        em.flush();
        em.clear(); // Clear cache for at sikre læsning fra DB

        // Assert
        Booking fromDb = bookingRepo.findById(updated.getId()).orElseThrow();
        assertThat(fromDb.getParticipants()).isEqualTo(5);
        assertThat(fromDb.getBookingStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(fromDb.getInstructorName()).isEqualTo("Ny Instruktør");
    }



    // Helper metode til at oprette test-bookinger
    private Booking createBooking(LocalDateTime start, int participants,
                                  BookingStatus status) {
        Booking b = new Booking();
        b.setActivity(testActivity);
        b.setCustomer(testCustomer);
        b.setStartDateTime(start);
        b.setParticipants(participants);
        b.setBookingStatus(status);
        return b;
    }
}