package com.example.miniProjekt.controller;

import com.example.miniProjekt.service.ProductServiceDto;
import com.example.miniProjekt.web.dto.ProductRequest;
import com.example.miniProjekt.web.dto.ProductResponse;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test alle endpoints i ProductController:
 * - GET /api/product (list)
 * - GET /api/product/{id} (getOne)
 * - POST /api/product (create)
 * - PUT /api/product/{id} (update)
 * - DELETE /api/product/{id} (delete)
 */
@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ProductServiceDto service;

    // ========== GET /api/product (LIST) ==========

    @Test
    void list_returns200AndJson() throws Exception {
        // Arrange
        ProductResponse p1 = new ProductResponse(
                1L, "Cola", new BigDecimal("25"), true
        );
        ProductResponse p2 = new ProductResponse(
                2L, "Fanta", new BigDecimal("20"), true
        );

        given(service.list(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(p1, p2)));

        // Act & Assert
        mvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("Cola"))
                .andExpect(jsonPath("$.content[0].price").value(25))
                .andExpect(jsonPath("$.content[1].name").value("Fanta"));
    }

    @Test
    void list_emptyDatabase_returnsEmptyPage() throws Exception {
        // Arrange
        given(service.list(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of()));

        // Act & Assert
        mvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    // ========== GET /api/product/{id} (GET ONE) ==========

    @Test
    void getOne_existingId_returns200AndJson() throws Exception {
        // Arrange
        ProductResponse product = new ProductResponse(
                1L, "Sprite", new BigDecimal("22.50"), true
        );

        given(service.get(1L)).willReturn(product);

        // Act & Assert
        mvc.perform(get("/api/product/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sprite"))
                .andExpect(jsonPath("$.price").value(22.50))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    // ========== POST /api/product (CREATE) ==========

    @Test
    void create_validData_returns201WithLocation() throws Exception {
        // Arrange
        ProductResponse created = new ProductResponse(
                10L, "Ny Cola", new BigDecimal("30"), true
        );

        given(service.create(any(ProductRequest.class))).willReturn(created);

        String json = """
            {
              "name": "Ny Cola",
              "price": 30,
              "isActive": true
            }
        """;

        // Act & Assert
        mvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/product/10")))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Ny Cola"));

        verify(service).create(any(ProductRequest.class));
    }


    // ========== PUT /api/product/{id} (UPDATE) ==========

    @Test
    void update_validData_returns200() throws Exception {
        // Arrange
        ProductResponse updated = new ProductResponse(
                1L, "Opdateret Navn", new BigDecimal("35"), false
        );

        given(service.update(any(Long.class), any(ProductRequest.class)))
                .willReturn(updated);

        String json = """
            {
              "name": "Opdateret Navn",
              "price": 35,
              "isActive": false
            }
        """;

        // Act & Assert
        mvc.perform(put("/api/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Opdateret Navn"))
                .andExpect(jsonPath("$.price").value(35))
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void update_invalidJson_returns400() throws Exception {
        // Arrange - malformed JSON
        String json = "{ invalid json }";

        // Act & Assert
        mvc.perform(put("/api/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // ========== DELETE /api/product/{id} (DELETE) ==========

    @Test
    void delete_existingId_returns204() throws Exception {
        // Act & Assert
        mvc.perform(delete("/api/product/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    // ========== HEADER & CONTENT TYPE TESTS ==========

    @Test
    void create_setsCorrectContentType() throws Exception {
        // Arrange
        ProductResponse created = new ProductResponse(
                1L, "Test", new BigDecimal("10"), true
        );
        given(service.create(any(ProductRequest.class))).willReturn(created);

        String json = """
            {
              "name": "Test",
              "price": 10,
              "isActive": true
            }
        """;

        // Act & Assert - verificer Content-Type header
        mvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void list_acceptsPageableParameters() throws Exception {
        // Arrange
        given(service.list(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of()));

        // Act & Assert - test pagination parameters
        mvc.perform(get("/api/product")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk());
    }
}