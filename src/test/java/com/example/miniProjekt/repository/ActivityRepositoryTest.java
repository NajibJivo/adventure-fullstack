package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.Activity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository test med @DataJpaTest:
 * - Starter kun JPA-laget (ikke hele Spring context)
 * - Bruger H2 in-memory database
 * - Hver test kører i transaktion der rulles tilbage
 */
@DataJpaTest
class ActivityRepositoryTest {

    @Autowired
    ActivityRepository repo;

    @Test
    void saveAndFindById_works() {
        // Arrange
        Activity activity = new Activity();
        activity.setName("Gokart");
        activity.setDescription("Hurtig racing");
        activity.setPrice(new BigDecimal("299"));
        activity.setDuration(30);
        activity.setMinAge(12);
        activity.setMinHeight(150);
        activity.setAvailableFrom(LocalDateTime.now());
        activity.setAvailableTo(LocalDateTime.now().plusMonths(6));

        // Act
        Activity saved = repo.save(activity);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(repo.findById(saved.getId())).isPresent();
        assertThat(repo.findById(saved.getId()).get().getName())
                .isEqualTo("Gokart");
    }

    @Test
    void findByNameContainingIgnoreCase_findsMatching() {
        // Arrange
        repo.save(createActivity("Paintball", 199));
        repo.save(createActivity("Klatring", 149));
        repo.save(createActivity("Gokart Racing", 299));

        // Act
        Page<Activity> result = repo.findByNameContainingIgnoreCase(
                "paint",
                PageRequest.of(0, 10)
        );

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Paintball");
    }

    @Test
    void findByNameContainingIgnoreCase_isCaseInsensitive() {
        // Arrange
        repo.save(createActivity("GoKart", 299));

        // Act - søg med lowercase
        Page<Activity> result = repo.findByNameContainingIgnoreCase(
                "gokart",
                PageRequest.of(0, 10)
        );

        // Assert
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findAll_returnsAllActivities() {
        // Arrange
        repo.save(createActivity("Activity 1", 100));
        repo.save(createActivity("Activity 2", 200));
        repo.save(createActivity("Activity 3", 300));

        // Act
        var all = repo.findAll();

        // Assert
        assertThat(all).hasSize(3);
    }

    @Test
    void deleteById_removesActivity() {
        // Arrange
        Activity saved = repo.save(createActivity("ToDelete", 100));
        Long id = saved.getId();

        // Act
        repo.deleteById(id);

        // Assert
        assertThat(repo.findById(id)).isEmpty();
    }

    @Test
    void update_changesFields() {
        // Arrange
        Activity activity = repo.save(createActivity("Original", 100));

        // Act
        activity.setName("Updated");
        activity.setPrice(new BigDecimal("150"));
        Activity updated = repo.save(activity);

        // Assert
        assertThat(updated.getName()).isEqualTo("Updated");
        assertThat(updated.getPrice()).isEqualByComparingTo("150");
    }

    // Helper til at oprette test-aktiviteter
    private Activity createActivity(String name, double price) {
        Activity a = new Activity();
        a.setName(name);
        a.setDescription("Test beskrivelse");
        a.setPrice(new BigDecimal(price));
        a.setDuration(60);
        a.setMinAge(10);
        a.setMinHeight(120);
        return a;
    }
}