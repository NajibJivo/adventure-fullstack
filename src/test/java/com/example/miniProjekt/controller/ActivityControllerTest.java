package com.example.miniProjekt.controller;
import com.example.miniProjekt.model.Activity;


import com.example.miniProjekt.service.ActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// Loader kun web-laget for hurtige, fokuserede tests.
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Erstatter repo med en mock
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.closeTo;

@WebMvcTest(ActivityController.class)
/* Slår sikkerhedsfiltre fra i denne slice-test (ellers 401 Unauthorized) */
@AutoConfigureMockMvc(addFilters = false) //✅ undgå 401 i slice-test
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityService activityService;


    // GET /activities -> 200 + liste
    @Test
    void getAll_returns200_andList() throws Exception {
        Activity a1 = new Activity(); a1.setId(1L); a1.setName("Go-kart"); a1.setPrice(BigDecimal.valueOf(200));
        Activity a2 = new Activity(); a2.setId(2L); a2.setName("Minigolf"); a2.setPrice(BigDecimal.valueOf(75));

        given(activityService.findAll()).willReturn(List.of(a1, a2));

        mockMvc.perform(get("/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Go-kart")))
                .andExpect(jsonPath("$[1].name", is("Minigolf")));
    }

    // GET /activities/{id} -> 200 + body
    @Test
    void getById_returns200_andBody() throws Exception {
        long id = 7L;
        Activity a = new Activity(); a.setId(id); a.setName("Paintball"); a.setPrice(BigDecimal.valueOf(200));

        given(activityService.getByIdOrThrow(id)).willReturn(a);

        mockMvc.perform(get("/activities/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.name", is("Paintball")))
                .andExpect(jsonPath("$.price", closeTo(200.0, 0.0001)));
    }

    // GET /activities/{id} -> 404 når service kaster NOT_FOUND
    @Test
    void getById_returns404_whenMissing() throws Exception {
        long missingId = 9999L;
        given(activityService.getByIdOrThrow(missingId))
                .willThrow(new ResponseStatusException(NOT_FOUND, "Activity not found"));

        mockMvc.perform(get("/activities/{id}", missingId))
                .andExpect(status().isNotFound());
    }

    // POST /activities -> 201 Created + Location-header + body
    @Test
    void create_returns201_andLocation_andBody() throws Exception {
        Activity saved = new Activity();
        saved.setId(42L);
        saved.setName("New");
        saved.setPrice(BigDecimal.valueOf(99));

        given(activityService.create(any(Activity.class))).willReturn(saved);

        String body = """
            {"name":"New","price":99.0}
        """;

        mockMvc.perform(post("/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/activities/42")))
                .andExpect(jsonPath("$.id", is(42)))
                .andExpect(jsonPath("$.name", is("New")))
                .andExpect(jsonPath("$.price", closeTo(99.0, 0.0001)));
    }

    // PUT /activities/{id} -> 200 OK + opdateret body
    @Test
    void update_returns200_andBody() throws Exception {
        long id = 1L;
        Activity updated = new Activity();
        updated.setId(id);
        updated.setName("New Name");
        updated.setPrice(BigDecimal.valueOf(123.45));

        given(activityService.update(eq(id), any(Activity.class))).willReturn(updated);

        String body = """
            {"name":"New Name","price":123.45}
        """;

        mockMvc.perform(put("/activities/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.name", is("New Name")))
                .andExpect(jsonPath("$.price", closeTo(123.45, 0.0001)));
    }

    // DELETE /activities/{id} -> 204 No Content
    @Test
    void delete_returns204() throws Exception {
        long id = 5L;
        doNothing().when(activityService).delete(id);

        mockMvc.perform(delete("/activities/{id}", id))
                .andExpect(status().isNoContent());
    }

    // DELETE /activities/{id} -> 404 når service kaster NOT_FOUND
    @Test
    void delete_returns404_whenMissing() throws Exception {
        long missingId = 888L;
        doThrow(new ResponseStatusException(NOT_FOUND, "Activity not found"))
                .when(activityService).delete(missingId);

        mockMvc.perform(delete("/activities/{id}", missingId))
                .andExpect(status().isNotFound());
    }

}
