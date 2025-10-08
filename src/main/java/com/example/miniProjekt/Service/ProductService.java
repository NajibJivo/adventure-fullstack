package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Product;
import com.example.miniProjekt.repository.ProductRepository;
import com.example.miniProjekt.service.exceptions.ProductNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    /** CREATE **/
    @Transactional
    public Product create(Product input) {
        validate(input);
        input.setId(null);
        // precheck unik navn for pænere fejl end DB 500
        if (repo.existsByNameIgnoreCase(input.getName())) {
            throw new IllegalArgumentException("Product name already exists: " + input.getName());
        }
        try {
            return repo.save(input);
        } catch (DataIntegrityViolationException die) {
            // fallback hvis DB-constraint alligevel trigger
            throw new IllegalArgumentException("Product name must be unique", die);
        }
    }

    /** READ **/
    public List<Product> findAll() {
        return repo.findAll();
    }

    public Product getByIdOrThrow(Long id) {
        return repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    /** UPDATE **/
    @Transactional
    public Product update(Long id, Product input) {
        Product existing = getByIdOrThrow(id);

        // hvis navn ændres, tjek unik
        if (input.getName() != null && !input.getName().equalsIgnoreCase(existing.getName())) {
            if (repo.existsByNameIgnoreCase(input.getName())) {
                throw new IllegalArgumentException("Product name already exists: " + input.getName());
            }
        }

        // fuld opdatering (eller vælg felt-for-felt hvis du vil støtte partial updates)
        existing.setName(input.getName());
        existing.setPrice(input.getPrice());
        existing.setIsActive(input.getIsActive() == null ? existing.getIsActive() : input.getIsActive());

        validate(existing);
        return repo.save(existing);
    }

    /** DELETE **/
    @Transactional
    public void delete(Long id) {
        Product existing = getByIdOrThrow(id);
        repo.delete(existing);
    }

    private void validate(Product p) {
        if (p.getName() == null || p.getName().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (p.getPrice() == null) {
            throw new IllegalArgumentException("price is required");
        }
        if (p.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("price must be >= 0");
        }
        if (p.getIsActive() == null) {
            p.setIsActive(true); // default som i DB
        }
    }
}
