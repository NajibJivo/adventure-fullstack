package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.Customer;
import com.example.miniProjekt.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Repository test - fokus på custom queries og constraints:
 * - findByEmail
 * - existsByEmail
 * - findByUserRole
 * - findByUsername
 * - existsByUsername
 * - UNIQUE constraint på email
 */
@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository repo;

    // ========== BASIC CRUD ==========

    @Test
    void saveAndFindById_works() {
        // Arrange
        Customer customer = createCustomer(
                "Test Kunde", "test@example.com", "testuser", UserRole.CUSTOMER
        );

        // Act
        Customer saved = repo.save(customer);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(repo.findById(saved.getId())).isPresent();
    }

    // ========== findByEmail (CUSTOM QUERY) ==========

    @Test
    void findByEmail_existingEmail_returnsCustomer() {
        // Arrange
        Customer customer = createCustomer(
                "John Doe", "john@example.com", "john", UserRole.CUSTOMER
        );
        repo.save(customer);

        // Act
        Optional<Customer> result = repo.findByEmail("john@example.com");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void findByEmail_nonExistingEmail_returnsEmpty() {
        // Act
        Optional<Customer> result = repo.findByEmail("nonexistent@example.com");

        // Assert
        assertThat(result).isEmpty();
    }

    // ========== existsByEmail (CUSTOM QUERY) ==========

    @Test
    void existsByEmail_existingEmail_returnsTrue() {
        // Arrange
        Customer customer = createCustomer(
                "Jane", "jane@example.com", "jane", UserRole.CUSTOMER
        );
        repo.save(customer);

        // Act
        boolean exists = repo.existsByEmail("jane@example.com");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_nonExistingEmail_returnsFalse() {
        // Act
        boolean exists = repo.existsByEmail("notfound@example.com");

        // Assert
        assertThat(exists).isFalse();
    }

    // ========== findByUsername (CUSTOM QUERY) ==========

    @Test
    void findByUsername_existingUsername_returnsCustomer() {
        // Arrange
        Customer customer = createCustomer(
                "Alice", "alice@example.com", "alice123", UserRole.CUSTOMER
        );
        repo.save(customer);

        // Act
        Optional<Customer> result = repo.findByUsername("alice123");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void findByUsername_nonExistingUsername_returnsEmpty() {
        // Act
        Optional<Customer> result = repo.findByUsername("nonexistent");

        // Assert
        assertThat(result).isEmpty();
    }

    // ========== existsByUsername (CUSTOM QUERY) ==========

    @Test
    void existsByUsername_existingUsername_returnsTrue() {
        // Arrange
        Customer customer = createCustomer(
                "Bob", "bob@example.com", "bobby", UserRole.EMPLOYEE
        );
        repo.save(customer);

        // Act
        boolean exists = repo.existsByUsername("bobby");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByUsername_nonExistingUsername_returnsFalse() {
        // Act
        boolean exists = repo.existsByUsername("nouser");

        // Assert
        assertThat(exists).isFalse();
    }

    // ========== findByUserRole (CUSTOM QUERY) ==========

    @Test
    void findByUserRole_returnsCustomersWithRole() {
        // Arrange
        repo.save(createCustomer("Customer1", "c1@example.com", "c1", UserRole.CUSTOMER));
        repo.save(createCustomer("Customer2", "c2@example.com", "c2", UserRole.CUSTOMER));
        repo.save(createCustomer("Employee1", "e1@example.com", "e1", UserRole.EMPLOYEE));
        repo.save(createCustomer("Owner1", "o1@example.com", "o1", UserRole.OWNER));

        // Act
        List<Customer> customers = repo.findByUserRole(UserRole.CUSTOMER);
        List<Customer> employees = repo.findByUserRole(UserRole.EMPLOYEE);
        List<Customer> owners = repo.findByUserRole(UserRole.OWNER);

        // Assert
        assertThat(customers).hasSize(2);
        assertThat(employees).hasSize(1);
        assertThat(owners).hasSize(1);
        assertThat(customers).allMatch(c -> c.getUserRole() == UserRole.CUSTOMER);
    }

    // ========== UNIQUE CONSTRAINT på email ==========

    @Test
    void save_duplicateEmail_throwsException() {
        // Arrange
        Customer c1 = createCustomer(
                "First", "duplicate@example.com", "user1", UserRole.CUSTOMER
        );
        Customer c2 = createCustomer(
                "Second", "duplicate@example.com", "user2", UserRole.CUSTOMER
        );

        repo.save(c1);

        // Act & Assert - skal kaste exception pga. UNIQUE constraint
        assertThatThrownBy(() -> {
            repo.save(c2);
            repo.flush(); // tvinger database-operation
        }).hasMessageContaining("constraint");
    }

    // ========== UNIQUE CONSTRAINT på username ==========

    @Test
    void save_duplicateUsername_throwsException() {
        // Arrange
        Customer c1 = createCustomer(
                "First", "email1@example.com", "sameuser", UserRole.CUSTOMER
        );
        Customer c2 = createCustomer(
                "Second", "email2@example.com", "sameuser", UserRole.CUSTOMER
        );

        repo.save(c1);

        // Act & Assert
        assertThatThrownBy(() -> {
            repo.save(c2);
            repo.flush();
        }).hasMessageContaining("constraint");
    }

    // ========== UPDATE TESTS ==========

    @Test
    void update_changesFields() {
        // Arrange
        Customer customer = createCustomer(
                "Original", "original@example.com", "original", UserRole.CUSTOMER
        );
        Customer saved = repo.save(customer);

        // Act
        saved.setName("Updated Name");
        saved.setUserRole(UserRole.OWNER);
        Customer updated = repo.save(saved);

        // Assert
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getUserRole()).isEqualTo(UserRole.OWNER);
        assertThat(updated.getEmail()).isEqualTo("original@example.com"); // uændret
    }

    // ========== DELETE TESTS ==========

    @Test
    void delete_removesCustomer() {
        // Arrange
        Customer customer = createCustomer(
                "ToDelete", "delete@example.com", "delete", UserRole.CUSTOMER
        );
        Customer saved = repo.save(customer);
        Long id = saved.getId();

        // Act
        repo.deleteById(id);

        // Assert
        assertThat(repo.findById(id)).isEmpty();
    }

    // ========== HELPER ==========

    private Customer createCustomer(String name, String email,
                                    String username, UserRole role) {
        Customer c = new Customer();
        c.setName(name);
        c.setEmail(email);
        c.setPhone("12345678");
        c.setUsername(username);
        c.setPassword("hashed_password");
        c.setUserRole(role);
        return c;
    }
}