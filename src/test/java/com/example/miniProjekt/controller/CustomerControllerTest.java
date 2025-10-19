package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.UserRole;
import com.example.miniProjekt.service.CustomerService;
import com.example.miniProjekt.web.dto.CustomerRequest;
import com.example.miniProjekt.web.dto.CustomerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    CustomerService service;

    // ========== GET /api/customers (LIST) ==========

    @Test
    void list_returns200AndJson() throws Exception {
        // Arrange
        CustomerResponse c1 = new CustomerResponse(
                1L, "John Doe", "12345678", "john@example.com", UserRole.CUSTOMER
        );
        CustomerResponse c2 = new CustomerResponse(
                2L, "Jane Smith", "87654321", "jane@example.com", UserRole.EMPLOYEE
        );

        given(service.list()).willReturn(List.of(c1, c2));

        // Act & Assert
        mvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].userRole").value("CUSTOMER"))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));
    }

    @Test
    void list_emptyDatabase_returnsEmptyArray() throws Exception {
        // Arrange
        given(service.list()).willReturn(List.of());

        // Act & Assert
        mvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ========== GET /api/customers/{id} (GET ONE) ==========

    @Test
    void get_existingId_returns200AndJson() throws Exception {
        // Arrange
        CustomerResponse customer = new CustomerResponse(
                1L, "Alice Johnson", "11223344", "alice@example.com", UserRole.OWNER
        );

        given(service.get(1L)).willReturn(customer);

        // Act & Assert
        mvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice Johnson"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.userRole").value("OWNER"));
    }

    // ========== POST /api/customers (CREATE) ==========

    @Test
    void create_validData_returns201() throws Exception {
        // Arrange
        CustomerResponse created = new CustomerResponse(
                10L, "New Customer", "99887766", "new@example.com", UserRole.CUSTOMER
        );

        given(service.create(any(CustomerRequest.class))).willReturn(created);

        String json = """
            {
              "name": "New Customer",
              "phone": "99887766",
              "email": "new@example.com",
              "userRole": "CUSTOMER"
            }
        """;

        // Act & Assert
        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("New Customer"));

        verify(service).create(any(CustomerRequest.class));
    }


    @Test
    void create_invalidJson_returns400() throws Exception {
        // Arrange - malformed JSON
        String json = "{ invalid }";

        // Act & Assert
        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // ========== PUT /api/customers/{id} (UPDATE) ==========

    @Test
    void update_validData_returns200() throws Exception {
        // Arrange
        CustomerResponse updated = new CustomerResponse(
                1L, "Updated Name", "55555555", "updated@example.com", UserRole.EMPLOYEE
        );

        given(service.update(any(Long.class), any(CustomerRequest.class)))
                .willReturn(updated);

        String json = """
            {
              "name": "Updated Name",
              "phone": "55555555",
              "email": "updated@example.com",
              "userRole": "EMPLOYEE"
            }
        """;

        // Act & Assert
        mvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.userRole").value("EMPLOYEE"));
    }

    // ========== DELETE /api/customers/{id} (DELETE) ==========

    @Test
    void delete_existingId_returns204() throws Exception {
        // Act & Assert
        mvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    // ========== ADDITIONAL VALIDATION TESTS ==========

    @Test
    void create_validatesContentType() throws Exception {
        // Act & Assert - uden Content-Type header
        mvc.perform(post("/api/customers")
                        .content("{}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void list_returnsCorrectContentType() throws Exception {
        // Arrange
        given(service.list()).willReturn(List.of());

        // Act & Assert
        mvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}