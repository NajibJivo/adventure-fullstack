package com.example.miniProjekt.Controller;

import com.example.miniProjekt.Service.ActivityService;
import com.example.miniProjekt.entity.Activity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActivityController.class)
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityService activityService;

    @Autowired
    private ObjectMapper objectMapper;

    private Activity testActivity;
    private List<Activity> testActivities;

    @BeforeEach
    void setUp() {
        testActivity = new Activity("Test Activity", "Test Description", 10, 5, 30, 100.0, false);
        testActivity.setId(1L);

        Activity activity2 = new Activity("Activity 2", "Description 2", 12, 8, 45, 150.0, true);
        activity2.setId(2L);

        testActivities = Arrays.asList(testActivity, activity2);
    }

    @Test
    void getAllActivities_ShouldReturnListOfActivities() throws Exception {
        // Given
        when(activityService.getAllActivities()).thenReturn(testActivities);

        // When & Then
        mockMvc.perform(get("/api/activities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Activity")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Activity 2")));

        verify(activityService, times(1)).getAllActivities();
    }

    @Test
    void getActivityById_WhenActivityExists_ShouldReturnActivity() throws Exception {
        // Given
        when(activityService.getActivityById(1L)).thenReturn(Optional.of(testActivity));

        // When & Then
        mockMvc.perform(get("/api/activities/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Activity")))
                .andExpect(jsonPath("$.description", is("Test Description")));

        verify(activityService, times(1)).getActivityById(1L);
    }

    @Test
    void getActivityById_WhenActivityNotExists_ShouldReturn404() throws Exception {
        // Given
        when(activityService.getActivityById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/activities/999"))
                .andExpect(status().isNotFound());

        verify(activityService, times(1)).getActivityById(999L);
    }

    @Test
    void createActivity_WithValidData_ShouldReturnCreatedActivity() throws Exception {
        // Given
        Activity newActivity = new Activity("New Activity", "New Description", 8, 6, 25, 80.0, false);
        Activity savedActivity = new Activity("New Activity", "New Description", 8, 6, 25, 80.0, false);
        savedActivity.setId(3L);

        when(activityService.saveActivity(any(Activity.class))).thenReturn(savedActivity);

        // When & Then
        mockMvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newActivity)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("New Activity")));

        verify(activityService, times(1)).saveActivity(any(Activity.class));
    }

    @Test
    void updateActivity_WhenActivityExists_ShouldReturnUpdatedActivity() throws Exception {
        // Given
        Activity updatedActivity = new Activity("Updated Activity", "Updated Description", 12, 10, 40, 120.0, true);

        when(activityService.getActivityById(1L)).thenReturn(Optional.of(testActivity));
        when(activityService.saveActivity(any(Activity.class))).thenReturn(updatedActivity);

        // When & Then
        mockMvc.perform(put("/api/activities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedActivity)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Activity")));

        verify(activityService, times(1)).getActivityById(1L);
        verify(activityService, times(1)).saveActivity(any(Activity.class));
    }

    @Test
    void deleteActivity_WhenActivityExists_ShouldReturn200() throws Exception {
        // Given
        when(activityService.getActivityById(1L)).thenReturn(Optional.of(testActivity));

        // When & Then
        mockMvc.perform(delete("/api/activities/1"))
                .andExpect(status().isOk());

        verify(activityService, times(1)).getActivityById(1L);
        verify(activityService, times(1)).deleteActivity(1L);
    }

    @Test
    void getActivitiesForAge_ShouldReturnFilteredActivities() throws Exception {
        // Given
        when(activityService.getActivitiesForAge(10)).thenReturn(Arrays.asList(testActivity));

        // When & Then
        mockMvc.perform(get("/api/activities/for-age/10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Activity")));

        verify(activityService, times(1)).getActivitiesForAge(10);
    }
}