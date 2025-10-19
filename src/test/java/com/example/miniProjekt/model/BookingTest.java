package com.example.miniProjekt.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void constructor_setsAllFields() {
        // Arrange
        Activity activity = new Activity();
        activity.setId(1L);

        Customer customer = new Customer();
        customer.setId(10L);

        LocalDateTime start = LocalDateTime.of(2030, 6, 15, 14, 0);

        // Act
        Booking booking = new Booking(
                1L, activity, customer, start,
                4, BookingStatus.PENDING, "Test Instruktør"
        );

        // Assert
        assertEquals(1L, booking.getId());
        assertEquals(activity, booking.getActivity());
        assertEquals(customer, booking.getCustomer());
        assertEquals(start, booking.getStartDateTime());
        assertEquals(4, booking.getParticipants());
        assertEquals(BookingStatus.PENDING, booking.getBookingStatus());
        assertEquals("Test Instruktør", booking.getInstructorName());
    }

    @Test
    void setters_updateFields() {
        // Arrange
        Booking booking = new Booking();

        // Act
        booking.setParticipants(5);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setInstructorName("Ny Instruktør");

        // Assert
        assertEquals(5, booking.getParticipants());
        assertEquals(BookingStatus.CONFIRMED, booking.getBookingStatus());
        assertEquals("Ny Instruktør", booking.getInstructorName());
    }

    @Test
    void noArgsConstructor_createsEmptyBooking() {
        // Act
        Booking booking = new Booking();

        // Assert
        assertNull(booking.getId());
        assertNull(booking.getActivity());
        assertNull(booking.getCustomer());
    }
}