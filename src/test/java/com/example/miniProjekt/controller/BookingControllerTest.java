package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.BookingStatus;
import com.example.miniProjekt.service.BookingService;
import com.example.miniProjekt.web.dto.BookingResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc(addFilters = false) // slår Security-filtre fra i testen
class BookingControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    BookingService service;

    @Test
    void listReturnsOk() throws Exception {
        mvc.perform(get("/api/bookings"))
                .andExpect(status().isOk());
    }

    @Test
    void listReturnsOkAndJsonBody() throws Exception {
        // 1) Arrange (stub)
        var r1 = new BookingResponse(1L, 10L, 100L,
                LocalDateTime.of(2030,1,1,10,0),
                4, BookingStatus.PENDING, "Alice");

        var r2 = new BookingResponse(2L, 11L, 101L,
                LocalDateTime.of(2030,1,2,10,0),
                2, BookingStatus.CONFIRMED, "Bob");
        when(service.list()).thenReturn(List.of(r1, r2));

        // 2) Act + Assert
        mvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookingStatus").value("PENDING"));
    }

    // ========== GET /api/bookings/{id} (GET ONE) ==========

    @Test
    void get_existingId_returns200AndJson() throws Exception {
        // Arrange
        BookingResponse booking = new BookingResponse(
                1L, 5L, 10L,
                LocalDateTime.of(2030, 6, 15, 14, 0),
                3, BookingStatus.CONFIRMED, "Test Instruktør"
        );

        when(service.get(1L)).thenReturn(booking);

        // Act & Assert
        mvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.participants").value(3))
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"));
    }

    // ========== POST /api/bookings (CREATE) ==========

    @Test
    void create_validData_returns201() throws Exception {
        // Arrange
        BookingResponse created = new BookingResponse(
                10L, 1L, 2L,
                LocalDateTime.of(2030, 7, 1, 10, 0),
                5, BookingStatus.PENDING, null
        );

        when(service.create(any())).thenReturn(created);

        String json = """
            {
              "activityId": 1,
              "customerId": 2,
              "startDatetime": "2030-07-01T10:00:00",
              "participants": 5,
              "bookingStatus": "PENDING"
            }
        """;

        // Act & Assert
        mvc.perform(post("/api/bookings")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.participants").value(5));
    }

    // ========== PUT /api/bookings/{id} (UPDATE) ==========

    @Test
    void update_validData_returns200() throws Exception {
        // Arrange
        BookingResponse updated = new BookingResponse(
                1L, 1L, 2L,
                LocalDateTime.of(2030, 7, 2, 15, 0),
                8, BookingStatus.CONFIRMED, "Ny Instruktør"
        );

        when(service.update(any(Long.class), any())).thenReturn(updated);

        String json = """
            {
              "activityId": 1,
              "customerId": 2,
              "startDatetime": "2030-07-02T15:00:00",
              "participants": 8,
              "bookingStatus": "CONFIRMED",
              "instructorName": "Ny Instruktør"
            }
        """;

        // Act & Assert
        mvc.perform(put("/api/bookings/1")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participants").value(8))
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"));
    }

    // ========== DELETE /api/bookings/{id} (DELETE) ==========

    @Test
    void delete_existingId_returns204() throws Exception {
        // Act & Assert
        mvc.perform(delete("/api/bookings/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    // ========== PUT /api/bookings/{id}/cancel (CANCEL) ==========

    @Test
    void cancel_existingId_returns200() throws Exception {
        // Arrange
        BookingResponse cancelled = new BookingResponse(
                1L, 1L, 2L,
                LocalDateTime.of(2030, 7, 1, 10, 0),
                5, BookingStatus.CANCELLED, null
        );

        when(service.cancel(1L)).thenReturn(cancelled);

        // Act & Assert
        mvc.perform(put("/api/bookings/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingStatus").value("CANCELLED"));
    }
}
