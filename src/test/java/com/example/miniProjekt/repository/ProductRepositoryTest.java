package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository repo;

    // ========== BASIC CRUD ==========

    @Test
    void saveAndFindById_works() {
        // Arrange
        Product product = createProduct("Cola", 25.50);

        // Act
        Product saved = repo.save(product);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(repo.findById(saved.getId())).isPresent();
    }

    // ========== existsByNameIgnoreCase (CUSTOM QUERY) ==========

    @Test
    void existsByNameIgnoreCase_existingName_returnsTrue() {
        // Arrange
        repo.save(createProduct("Coca Cola", 25));

        // Act
        boolean exists = repo.existsByNameIgnoreCase("Coca Cola");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_isCaseInsensitive() {
        // Arrange
        repo.save(createProduct("Fanta", 20));

        // Act & Assert
        assertThat(repo.existsByNameIgnoreCase("fanta")).isTrue();
        assertThat(repo.existsByNameIgnoreCase("FANTA")).isTrue();
        assertThat(repo.existsByNameIgnoreCase("FaNtA")).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_nonExistingName_returnsFalse() {
        // Act
        boolean exists = repo.existsByNameIgnoreCase("NonExistent");

        // Assert
        assertThat(exists).isFalse();
    }

    // ========== UNIQUE CONSTRAINT på name ==========

    @Test
    void save_duplicateName_throwsException() {
        // Arrange
        Product p1 = createProduct("Sprite", 22);
        Product p2 = createProduct("Sprite", 25);

        repo.save(p1);

        // Act & Assert
        assertThatThrownBy(() -> {
            repo.save(p2);
            repo.flush(); // tvinger database-operation
        }).hasMessageContaining("constraint");
    }

    @Test
    void save_differentCaseNames_allowsBoth() {
        // Arrange - MySQL ser "pepsi" og "PEPSI" som forskellige
        Product p1 = createProduct("pepsi", 20);
        Product p2 = createProduct("PEPSI", 22);

        // Act
        repo.save(p1);
        repo.save(p2);
        repo.flush();

        // Assert - begge gemmes uden fejl
        assertThat(repo.findAll()).hasSize(2);
    }

    // ========== UPDATE TESTS ==========

    @Test
    void update_changesFields() {
        // Arrange
        Product product = repo.save(createProduct("Original", 10));

        // Act
        product.setName("Updated");
        product.setPrice(new BigDecimal("15.50"));
        product.setIsActive(false);
        Product updated = repo.save(product);

        // Assert
        assertThat(updated.getName()).isEqualTo("Updated");
        assertThat(updated.getPrice()).isEqualByComparingTo("15.50");
        assertThat(updated.getIsActive()).isFalse();
    }

    // ========== DELETE TESTS ==========

    @Test
    void delete_removesProduct() {
        // Arrange
        Product product = repo.save(createProduct("ToDelete", 10));
        Long id = product.getId();

        // Act
        repo.deleteById(id);

        // Assert
        assertThat(repo.findById(id)).isEmpty();
    }

    @Test
    void findAll_returnsAllProducts() {
        // Arrange
        repo.save(createProduct("Product1", 10));
        repo.save(createProduct("Product2", 20));
        repo.save(createProduct("Product3", 30));

        // Act
        var all = repo.findAll();

        // Assert
        assertThat(all).hasSize(3);
    }

    // ========== HELPER ==========

    private Product createProduct(String name, double price) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(new BigDecimal(price));
        p.setIsActive(true);
        return p;
    }
}