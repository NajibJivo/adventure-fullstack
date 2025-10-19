package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.repository.ActivityRepository;
import com.example.miniProjekt.service.exceptions.ActivityNotFoundException;
import com.example.miniProjekt.web.dto.ActivityRequest;
import com.example.miniProjekt.web.dto.ActivityResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test alle metoder i ActivityServiceDto
 */
@ExtendWith(MockitoExtension.class)
class ActivityServiceDtoTest {

    @Mock
    ActivityRepository repo;

    @InjectMocks
    ActivityServiceDto service;

    // ========== CREATE ==========

    @Test
    void create_validActivity_savesAndReturnsDto() {
        // Arrange
        ActivityRequest req = new ActivityRequest(
                "Gokart", "Sjov racing", new BigDecimal("299"),
                30, 12, 150, null, null, null
        );

        when(repo.save(any(Activity.class))).thenAnswer(inv -> {
            Activity a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        // Act
        ActivityResponse result = service.create(req);

        // Assert
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Gokart");
        assertThat(result.price()).isEqualByComparingTo("299");
        verify(repo).save(any(Activity.class));
    }

    @Test
    void create_missingName_throwsException() {
        // Arrange
        ActivityRequest req = new ActivityRequest(
                null, "Beskrivelse", new BigDecimal("100"),
                30, 10, 120, null, null, null
        );

        // Act & Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name is required");

        verify(repo, never()).save(any());
    }

    @Test
    void create_blankName_throwsException() {
        // Arrange
        ActivityRequest req = new ActivityRequest(
                "   ", "Beskrivelse", new BigDecimal("100"),
                30, 10, 120, null, null, null
        );

        // Act & Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name is required");
    }

    @Test
    void create_negativePrice_throwsException() {
        // Arrange
        ActivityRequest req = new ActivityRequest(
                "Test", "Beskrivelse", new BigDecimal("-10"),
                30, 10, 120, null, null, null
        );

        // Act & Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price must be >= 0");
    }

    @Test
    void create_zeroDuration_throwsException() {
        // Arrange
        ActivityRequest req = new ActivityRequest(
                "Test", "Beskrivelse", new BigDecimal("100"),
                0, 10, 120, null, null, null
        );

        // Act & Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duration must be > 0");
    }

    @Test
    void create_negativeMinAge_throwsException() {
        // Arrange
        ActivityRequest req = new ActivityRequest(
                "Test", "Beskrivelse", new BigDecimal("100"),
                30, -5, 120, null, null, null
        );

        // Act & Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("minAge must be >= 0");
    }

    @Test
    void create_invalidDateRange_throwsException() {
        // Arrange - availableFrom er EFTER availableTo
        LocalDateTime from = LocalDateTime.of(2025, 12, 31, 23, 59);
        LocalDateTime to = LocalDateTime.of(2025, 1, 1, 0, 0);

        ActivityRequest req = new ActivityRequest(
                "Test", "Beskrivelse", new BigDecimal("100"),
                30, 10, 120, from, to, null
        );

        // Act & Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("availableFrom must be before availableTo");
    }

    // ========== READ ==========

    @Test
    void list_returnsPageOfActivities() {
        // Arrange
        Activity a1 = createActivity(1L, "Gokart", 299);
        Activity a2 = createActivity(2L, "Paintball", 199);

        when(repo.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(a1, a2)));

        // Act
        Page<ActivityResponse> result = service.list(PageRequest.of(0, 10));

        // Assert
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).name()).isEqualTo("Gokart");
    }

    @Test
    void getByIdOrThrow_existingId_returnsActivity() {
        // Arrange
        Activity activity = createActivity(1L, "Klatring", 149);
        when(repo.findById(1L)).thenReturn(Optional.of(activity));

        // Act
        Activity result = service.getByIdOrThrow(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Klatring");
    }

    @Test
    void getByIdOrThrow_nonExistingId_throwsException() {
        // Arrange
        when(repo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.getByIdOrThrow(999L))
                .isInstanceOf(ActivityNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ========== UPDATE ==========

    @Test
    void update_validData_updatesActivity() {
        // Arrange
        Activity existing = createActivity(1L, "Gammel", 100);
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Activity.class))).thenAnswer(inv -> inv.getArgument(0));

        ActivityRequest req = new ActivityRequest(
                "Opdateret", "Ny beskrivelse", new BigDecimal("200"),
                45, 15, 140, null, null, null
        );

        // Act
        ActivityResponse result = service.update(1L, req);

        // Assert
        assertThat(result.name()).isEqualTo("Opdateret");
        assertThat(result.price()).isEqualByComparingTo("200");
        verify(repo).save(any(Activity.class));
    }

    @Test
    void update_nonExistingId_throwsException() {
        // Arrange
        when(repo.findById(999L)).thenReturn(Optional.empty());
        ActivityRequest req = new ActivityRequest(
                "Test", "Beskrivelse", new BigDecimal("100"),
                30, 10, 120, null, null, null
        );

        // Act & Assert
        assertThatThrownBy(() -> service.update(999L, req))
                .isInstanceOf(ActivityNotFoundException.class);
    }

    // ========== DELETE ==========

    @Test
    void delete_existingId_deletesActivity() {
        // Arrange
        Activity existing = createActivity(1L, "ToDelete", 100);
        when(repo.findById(1L)).thenReturn(Optional.of(existing));

        // Act
        service.delete(1L);

        // Assert
        verify(repo).delete(existing);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        // Arrange
        when(repo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(ActivityNotFoundException.class);
    }

    // ========== SEARCH ==========

    @Test
    void search_withQuery_returnsFilteredResults() {
        // Arrange
        Activity match = createActivity(1L, "Gokart Racing", 299);
        when(repo.findByNameContainingIgnoreCase(eq("gokart"), any()))
                .thenReturn(new PageImpl<>(List.of(match)));

        // Act
        Page<ActivityResponse> result = service.search("gokart", PageRequest.of(0, 10));

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).contains("Gokart");
    }

    // ========== HELPER ==========

    private Activity createActivity(Long id, String name, double price) {
        Activity a = new Activity();
        a.setId(id);
        a.setName(name);
        a.setDescription("Test beskrivelse");
        a.setPrice(new BigDecimal(price));
        a.setDuration(60);
        a.setMinAge(10);
        a.setMinHeight(120);
        return a;
    }
}