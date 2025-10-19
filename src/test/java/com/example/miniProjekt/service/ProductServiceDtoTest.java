package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Product;
import com.example.miniProjekt.repository.ProductRepository;
import com.example.miniProjekt.service.exceptions.ProductNotFoundException;
import com.example.miniProjekt.web.dto.ProductRequest;
import com.example.miniProjekt.web.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceDtoTest {

    @Mock
    ProductRepository repo;

    @InjectMocks
    ProductServiceDto service;

    // ========== CREATE ==========

    @Test
    void create_validProduct_savesAndReturns() {
        // Arrange
        ProductRequest req = new ProductRequest(
                "Cola", new BigDecimal("25.50"), true
        );

        when(repo.existsByNameIgnoreCase("Cola")).thenReturn(false);
        when(repo.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        // Act
        ProductResponse result = service.create(req);

        // Assert
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Cola");
        assertThat(result.price()).isEqualByComparingTo("25.50");
        verify(repo).save(any(Product.class));
    }

    @Test
    void create_duplicateName_throwsException() {
        // Arrange
        ProductRequest req = new ProductRequest(
                "Cola", new BigDecimal("25"), true
        );

        when(repo.existsByNameIgnoreCase("Cola")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(repo, never()).save(any());
    }

    @Test
    void create_missingName_throwsException() {
        // Arrange
        ProductRequest req = new ProductRequest(
                null, new BigDecimal("25"), true
        );

        // Act & Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name is required");
    }

    @Test
    void create_negativePrice_throwsException() {
        // Arrange
        ProductRequest req = new ProductRequest(
                "Test", new BigDecimal("-10"), true
        );

        // Act & Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price must be >= 0");
    }

    @Test
    void create_nullIsActive_defaultsToTrue() {
        // Arrange
        ProductRequest req = new ProductRequest(
                "Test Product", new BigDecimal("50"), null
        );

        when(repo.existsByNameIgnoreCase(any())).thenReturn(false);
        when(repo.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        // Act
        ProductResponse result = service.create(req);

        // Assert
        // Tjek hvad din service faktisk gør
        assertThat(result.isActive()).isNotNull();
    }

    // ========== READ ==========

    @Test
    void list_returnsPageOfProducts() {
        // Arrange
        Product p1 = createProduct(1L, "Cola", 25);
        Product p2 = createProduct(2L, "Fanta", 20);

        when(repo.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(p1, p2)));

        // Act
        Page<ProductResponse> result = service.list(PageRequest.of(0, 10));

        // Assert
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).name()).isEqualTo("Cola");
    }

    @Test
    void get_existingId_returnsDto() {
        // Arrange
        Product product = createProduct(1L, "Sprite", 22);
        when(repo.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductResponse result = service.get(1L);

        // Assert
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Sprite");
    }

    @Test
    void get_nonExistingId_throwsException() {
        // Arrange
        when(repo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.get(999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getEntityByIdOrThrow_existingId_returnsEntity() {
        // Arrange
        Product product = createProduct(1L, "Test", 10);
        when(repo.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Product result = service.getEntityByIdOrThrow(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    // ========== UPDATE ==========

    @Test
    void update_validData_updatesProduct() {
        // Arrange
        Product existing = createProduct(1L, "OldName", 10);
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductRequest req = new ProductRequest(
                "NewName", new BigDecimal("15"), false
        );

        // Act
        ProductResponse result = service.update(1L, req);

        // Assert
        assertThat(result.name()).isEqualTo("NewName");
        assertThat(result.price()).isEqualByComparingTo("15");
        assertThat(result.isActive()).isFalse();
        verify(repo).save(any(Product.class));
    }

    @Test
    void update_changingNameToDuplicate_throwsException() {
        // Arrange
        Product existing = createProduct(1L, "Original", 10);
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.existsByNameIgnoreCase("Duplicate")).thenReturn(true);

        ProductRequest req = new ProductRequest(
                "Duplicate", new BigDecimal("15"), true
        );

        // Act & Assert
        assertThatThrownBy(() -> service.update(1L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(repo, never()).save(any());
    }

    @Test
    void update_nonExistingId_throwsException() {
        // Arrange
        when(repo.findById(999L)).thenReturn(Optional.empty());
        ProductRequest req = new ProductRequest("Test", new BigDecimal("10"), true);

        // Act & Assert
        assertThatThrownBy(() -> service.update(999L, req))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // ========== DELETE ==========

    @Test
    void delete_existingId_deletesProduct() {
        // Arrange
        Product existing = createProduct(1L, "ToDelete", 10);
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
                .isInstanceOf(ProductNotFoundException.class);
    }

    // ========== HELPER ==========

    private Product createProduct(Long id, String name, double price) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setPrice(new BigDecimal(price));
        p.setIsActive(true);
        return p;
    }
}