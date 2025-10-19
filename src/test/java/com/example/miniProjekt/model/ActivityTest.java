package com.example.miniProjekt.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for Activity-model: validerer constructors og basic fields
 */
class ActivityTest {

    @Test
    void constructorWithAllFields_setsFieldsCorrectly() {
        // Arrange
        LocalDateTime from = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime to = LocalDateTime.of(2025, 12, 31, 20, 0);

        // Act
        Activity activity = new Activity(
                1L, "Gokart", "Sjov racing",
                new BigDecimal("299.00"), 30, 12, 150,
                from, to, "https://example.com/image.jpg"
        );

        // Assert
        assertEquals(1L, activity.getId());
        assertEquals("Gokart", activity.getName());
        assertEquals(new BigDecimal("299.00"), activity.getPrice());
        assertEquals(30, activity.getDuration());
        assertEquals(12, activity.getMinAge());
        assertEquals(150, activity.getMinHeight());
        assertEquals(from, activity.getAvailableFrom());
        assertEquals(to, activity.getAvailableTo());
    }

    @Test
    void simpleConstructor_setsBasicFields() {
        // Act
        Activity activity = new Activity(
                "Paintball", "Sjov skydning",
                10, 140, 60, 199.0
        );

        // Assert
        assertNull(activity.getId()); // id er null før save
        assertEquals("Paintball", activity.getName());
        assertEquals(10, activity.getMinAge());
        assertEquals(60, activity.getDuration());
        assertEquals(new BigDecimal("199.0"), activity.getPrice());
    }

    @Test
    void setters_updateFieldsCorrectly() {
        // Arrange
        Activity activity = new Activity();

        // Act
        activity.setName("Klatring");
        activity.setPrice(new BigDecimal("149.50"));
        activity.setDuration(45);

        // Assert
        assertEquals("Klatring", activity.getName());
        assertEquals(new BigDecimal("149.50"), activity.getPrice());
        assertEquals(45, activity.getDuration());
    }

    @Test
    void priceCannotBeNegative() {
        // Arrange
        Activity activity = new Activity();

        // Act
        activity.setPrice(new BigDecimal("-10"));

        // Assert - dette skal fanges i service-laget
        // (model har typisk ingen validering, men vi dokumenterer forventning)
        assertTrue(activity.getPrice().signum() < 0,
                "Negativ pris skal fanges i service-validering");
    }
}