package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.service.ActivityServiceDto;
import com.example.miniProjekt.web.dto.ActivityRequest;
import com.example.miniProjekt.web.dto.ActivityResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller test med @WebMvcTest:
 * - Tester kun web-laget (ikke services/repos)
 * - Mocker service-dependencies
 * - Verificerer HTTP statuskoder, JSON-struktur, headers
 */
@WebMvcTest(ActivityController.class)
@AutoConfigureMockMvc(addFilters = false) // Slår Security fra i tests
class ActivityControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ActivityServiceDto service;

    @Test
    void getAllActivities_returns200AndJson() throws Exception {
        // Arrange
        ActivityResponse a1 = new ActivityResponse(
                1L, "Gokart", "Hurtig racing",
                new BigDecimal("299"), 30, 12, 150,
                LocalDateTime.now(), LocalDateTime.now().plusMonths(6),
                "https://example.com/gokart.jpg"
        );
        ActivityResponse a2 = new ActivityResponse(
                2L, "Paintball", "Sjov skydning",
                new BigDecimal("199"), 60, 10, 140,
                null, null, null
        );

        given(service.list(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(a1, a2)));

        // Act & Assert
        mvc.perform(get("/api/activities"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("Gokart"))
                .andExpect(jsonPath("$.content[0].price").value(299))
                .andExpect(jsonPath("$.content[1].name").value("Paintball"));
    }

    @Test
    void getActivityById_existingId_returns200() throws Exception {
        // Arrange
        Activity activity = new Activity();
        activity.setId(1L);
        activity.setName("Klatring");
        activity.setPrice(new BigDecimal("149"));
        activity.setDuration(45);
        activity.setMinAge(8);
        activity.setMinHeight(120);

        given(service.getByIdOrThrow(1L)).willReturn(activity);

        // Act & Assert
        mvc.perform(get("/api/activities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Klatring"))
                .andExpect(jsonPath("$.price").value(149));
    }

    @Test
    void createActivity_validData_returns201WithLocation() throws Exception {
        // Arrange
        ActivityResponse created = new ActivityResponse(
                10L, "Ny Aktivitet", "Test beskrivelse",
                new BigDecimal("250"), 60, 12, 150,
                null, null, null
        );

        given(service.create(any(ActivityRequest.class))).willReturn(created);

        String json = """
            {
              "name": "Ny Aktivitet",
              "description": "Test beskrivelse",
              "price": 250,
              "duration": 60,
              "minAge": 12,
              "minHeight": 150
            }
        """;

        // Act & Assert
        mvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/activities/10")))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Ny Aktivitet"));

        verify(service).create(any(ActivityRequest.class));
    }

    @Test
    void updateActivity_existingId_returns200() throws Exception {
        // Arrange
        ActivityResponse updated = new ActivityResponse(
                1L, "Opdateret navn", "Ny beskrivelse",
                new BigDecimal("350"), 45, 15, 160,
                null, null, null
        );

        given(service.update(any(Long.class), any(ActivityRequest.class)))
                .willReturn(updated);

        String json = """
            {
              "name": "Opdateret navn",
              "description": "Ny beskrivelse",
              "price": 350,
              "duration": 45,
              "minAge": 15,
              "minHeight": 160
            }
        """;

        // Act & Assert
        mvc.perform(put("/api/activities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Opdateret navn"))
                .andExpect(jsonPath("$.price").value(350));
    }

    @Test
    void deleteActivity_existingId_returns204() throws Exception {
        // Act & Assert
        mvc.perform(delete("/api/activities/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void search_withQuery_returnsFilteredResults() throws Exception {
        // Arrange
        ActivityResponse match = new ActivityResponse(
                1L, "Gokart Racing", "Hurtig",
                new BigDecimal("299"), 30, 12, 150,
                null, null, null
        );

        given(service.search(any(String.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(match)));

        // Act & Assert
        mvc.perform(get("/api/activities/search")
                        .param("q", "gokart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Gokart Racing"));
    }

    @Test
    void getAllActivities_emptyDatabase_returnsEmptyPage() throws Exception {
        // Arrange
        given(service.list(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of()));

        // Act & Assert
        mvc.perform(get("/api/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }
}