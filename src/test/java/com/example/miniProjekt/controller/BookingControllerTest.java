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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

}