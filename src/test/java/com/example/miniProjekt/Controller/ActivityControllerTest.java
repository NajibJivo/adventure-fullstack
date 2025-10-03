package com.example.miniProjekt.controller;

import com.example.miniProjekt.controller.ActivityController;
import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.repository.ActivityRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// Loader kun web-laget for hurtige, fokuserede tests.
import org.springframework.boot.test.mock.mockito.MockBean; // Erstatter repo med en mock
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActivityController.class)
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityRepository activityRepository;

    // 1) PUT /{id} -> 200 + opdateret body (id findes)
    @Test
    void update_returns200_andBody_whenIdExists() throws Exception {
        long id = 1L;

        Activity existing = new Activity();
        existing.setId(id);
        existing.setName("Old");
        existing.setPrice(50.0);
        when(activityRepository.findById(id)).thenReturn(Optional.of(existing));

        Activity saved = new Activity();
        saved.setId(id);
        saved.setName("New Name");
        saved.setPrice(123.45);
        when(activityRepository.save(existing)).thenReturn(saved);

        String body = """
                    {"name":"New Name","price":123.45}
                """;

        mockMvc.perform(
                put("/activities/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.name", is("New Name")))
                .andExpect(jsonPath("$.price", is(123.45)));
    }

    // 2) PUT /{id} -> 200 + oprettet body (id findes ikke) [upsert]
    @Test
    void update_returns200_andCreates_whenIdMissing_upsertBehavior() throws Exception {
        long missingId = 999L;

        when(activityRepository.findById(missingId)).thenReturn(Optional.empty());

        Activity created = new Activity();
        created.setId(missingId);
        created.setName("Brand New");
        created.setPrice(77.0);
        when(activityRepository.save(any(Activity.class))).thenReturn(created);

        String body = """
                    {"name":"Brand New","price":77.0}
                """;

        mockMvc.perform(
                put("/activities/{id}", missingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) missingId)))
                .andExpect(jsonPath("$.name", is("Brand New")))
                .andExpect(jsonPath("$.price", is(77.0)));
    }

    // 3) POST / -> 200 + body (ingen 201/Location i denne controller)
    @Test
    void create_returns200_andBody_whenValid() throws Exception {
        Activity saved = new Activity();
        saved.setId(42L);
        saved.setName("New");
        saved.setPrice(99.0);
        when(activityRepository.save(any(Activity.class))).thenReturn(saved);

        String body = """
                    {"name":"New","price":99.0}
                """;

        mockMvc.perform(
                post("/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(42)))
                .andExpect(jsonPath("$.name", is("New")))
                .andExpect(jsonPath("$.price", is(99.0)));
    }

    // 4) GET /{id} -> 200 + body (id findes)
    @Test
    void getById_returns200_andBody_whenExists() throws Exception {
        long id = 7L;
        Activity a = new Activity();
        a.setId(id);
        a.setName("Paintball");
        a.setPrice(200.0);
        when(activityRepository.findById(id)).thenReturn(Optional.of(a));

        mockMvc.perform(get("/activities/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.name", is("Paintball")))
                .andExpect(jsonPath("$.price", is(200.0)));
    }

    // 5) GET /{id} -> status afhænger af Spring-versionens håndtering af Optional
    // Typisk: Optional tom -> 404. Hvis du får 200 i dit setup, skift assertion til
    // isOk()
    @Test
    void getById_returns404_whenMissing() throws Exception {
        long missingId = 9999L;
        when(activityRepository.findById(missingId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/activities/{id}", missingId))
                .andExpect(status().isNotFound());
        // Hvis denne fejler med 200:
        // .andExpect(status().isOk());
        // valgfrit: .andExpect(content().string(""));
    }

    // 6) GET / -> 200 + liste
    @Test
    void getAll_returns200_andList() throws Exception {
        Activity a1 = new Activity();
        a1.setId(1L);
        a1.setName("Go-kart");
        a1.setPrice(150.0);
        Activity a2 = new Activity();
        a2.setId(2L);
        a2.setName("Minigolf");
        a2.setPrice(75.0);
        when(activityRepository.findAll()).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Go-kart")))
                .andExpect(jsonPath("$[1].name", is("Minigolf")));
    }
}
