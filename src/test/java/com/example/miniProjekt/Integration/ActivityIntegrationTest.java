package com.example.miniProjekt.Integration;

import com.example.miniProjekt.entity.Activity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ActivityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void completeActivityWorkflow_ShouldWork() throws Exception {
        // 1. Initialize test data
        mockMvc.perform(post("/api/activities/init-data"))
                .andExpect(status().isOk());

        // 2. Get all activities
        mockMvc.perform(get("/api/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));

        // 3. Create new activity
        Activity newActivity = new Activity("Integration Test Activity", "Test Description", 15, 6, 45, 175.0, true);

        String response = mockMvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newActivity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Activity"))
                .andReturn().getResponse().getContentAsString();

        // Extract ID from response
        Activity createdActivity = objectMapper.readValue(response, Activity.class);

        // 4. Get specific activity
        mockMvc.perform(get("/api/activities/" + createdActivity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Activity"));

        // 5. Update activity
        createdActivity.setName("Updated Integration Test Activity");
        mockMvc.perform(put("/api/activities/" + createdActivity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdActivity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Integration Test Activity"));

        // 6. Delete activity
        mockMvc.perform(delete("/api/activities/" + createdActivity.getId()))
                .andExpect(status().isOk());

        // 7. Verify deletion
        mockMvc.perform(get("/api/activities/" + createdActivity.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getActivitiesForAge_ShouldFilterCorrectly() throws Exception {
        // Initialize test data
        mockMvc.perform(post("/api/activities/init-data"))
                .andExpect(status().isOk());

        // Test age filtering
        mockMvc.perform(get("/api/activities/for-age/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }
}