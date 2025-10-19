package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Customer;
import com.example.miniProjekt.model.UserRole;
import com.example.miniProjekt.repository.CustomerRepository;
import com.example.miniProjekt.service.exceptions.CustomerNotFoundException;
import com.example.miniProjekt.web.dto.CustomerRequest;
import com.example.miniProjekt.web.dto.CustomerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test af CustomerService - fokus på validering og unikhedstjek
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    CustomerRepository repo;

    @InjectMocks
    CustomerService service;

    private Customer testCustomer;

    @BeforeEach
    void setup() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Kunde");
        testCustomer.setEmail("test@example.com");
        testCustomer.setPhone("12345678");
        testCustomer.setUserRole(UserRole.CUSTOMER);
    }

    @Test
    void create_validCustomer_savesAndReturnsResponse() {
        // Arrange
        CustomerRequest req = new CustomerRequest(
                "Ny Kunde", "87654321", "ny@example.com", UserRole.CUSTOMER
        );

        when(repo.existsByEmail("ny@example.com")).thenReturn(false);
        when(repo.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(2L);
            return c;
        });

        // Act
        CustomerResponse result = service.create(req);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo("Ny Kunde");
        assertThat(result.email()).isEqualTo("ny@example.com");
        verify(repo).save(any(Customer.class));
    }

    @Test
    void create_duplicateEmail_throwsException() {
        // Arrange
        CustomerRequest req = new CustomerRequest(
                "Duplikat", "11111111", "test@example.com", UserRole.CUSTOMER
        );

        when(repo.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("E-mail already in use");

        verify(repo, never()).save(any(Customer.class));
    }

    @Test
    void get_existingCustomer_returnsResponse() {
        // Arrange
        when(repo.findById(1L)).thenReturn(Optional.of(testCustomer));

        // Act
        CustomerResponse result = service.get(1L);

        // Assert
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Test Kunde");
        assertThat(result.email()).isEqualTo("test@example.com");
    }

    @Test
    void get_nonExistingCustomer_throwsException() {
        // Arrange
        when(repo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.get(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    void list_returnsAllCustomers() {
        // Arrange
        Customer c1 = testCustomer;
        Customer c2 = new Customer();
        c2.setId(2L);
        c2.setName("Anden kunde");
        c2.setEmail("anden@example.com");
        c2.setUserRole(UserRole.EMPLOYEE);

        when(repo.findAll()).thenReturn(List.of(c1, c2));

        // Act
        List<CustomerResponse> result = service.list();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).userRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(result.get(1).userRole()).isEqualTo(UserRole.EMPLOYEE);
    }

    @Test
    void update_validData_updatesCustomer() {
        // Arrange
        when(repo.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(repo.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerRequest req = new CustomerRequest(
                "Opdateret navn", "99999999", "test@example.com", UserRole.OWNER
        );

        // Act
        CustomerResponse result = service.update(1L, req);

        // Assert
        assertThat(result.name()).isEqualTo("Opdateret navn");
        assertThat(result.phone()).isEqualTo("99999999");
        assertThat(result.userRole()).isEqualTo(UserRole.OWNER);
        verify(repo).save(any(Customer.class));
    }

    @Test
    void update_changingEmailToExisting_throwsException() {
        // Arrange
        when(repo.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(repo.existsByEmail("existing@example.com")).thenReturn(true);

        CustomerRequest req = new CustomerRequest(
                "Test", "12345678", "existing@example.com", UserRole.CUSTOMER
        );

        // Act & Assert
        assertThatThrownBy(() -> service.update(1L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");

        verify(repo, never()).save(any(Customer.class));
    }

    @Test
    void update_sameEmail_allowsUpdate() {
        // Arrange - bruger opdaterer sit eget navn, men beholder emailen
        when(repo.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(repo.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerRequest req = new CustomerRequest(
                "Nyt navn", "12345678", "test@example.com", UserRole.CUSTOMER
        );

        // Act
        CustomerResponse result = service.update(1L, req);

        // Assert - skal IKKE tjekke existsByEmail når email er uændret
        assertThat(result.name()).isEqualTo("Nyt navn");
        verify(repo).save(any(Customer.class));
        verify(repo, never()).existsByEmail(any());
    }

    @Test
    void delete_existingCustomer_callsRepository() {
        // Arrange
        when(repo.existsById(1L)).thenReturn(true);

        // Act
        service.delete(1L);

        // Assert
        verify(repo).deleteById(1L);
    }

    @Test
    void delete_nonExistingCustomer_throwsException() {
        // Arrange
        when(repo.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Customer not found");

        verify(repo, never()).deleteById(any());
    }

    @Test
    void getByIdOrThrow_existingId_returnsEntity() {
        // Arrange
        when(repo.findById(1L)).thenReturn(Optional.of(testCustomer));

        // Act
        Customer result = service.getByIdOrThrow(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Kunde");
    }

    @Test
    void getByIdOrThrow_nonExistingId_throwsCustomException() {
        // Arrange
        when(repo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.getByIdOrThrow(999L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("999");
    }
}