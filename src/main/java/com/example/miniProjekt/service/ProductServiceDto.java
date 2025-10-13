package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Product;
import com.example.miniProjekt.repository.ProductRepository;
import com.example.miniProjekt.service.exceptions.ProductNotFoundException;
import com.example.miniProjekt.web.dto.ProductRequest;
import com.example.miniProjekt.web.dto.ProductResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * ProductService – forretningslogik for CRUD på Product.
 * Håndhæver bl.a. unikhed på navn og simple feltvalideringer (name/price/isActive).
 */
@Service
public class ProductServiceDto {
    private final ProductRepository repo;

    public ProductServiceDto(ProductRepository repo) {
        this.repo = repo;
    }

    /** CREATE -> DTO **/
    @Transactional
    public ProductResponse create(ProductRequest req) {
        Product input = apply(new Product(), req);
        validate(input);
        input.setId(null);

        if (repo.existsByNameIgnoreCase(input.getName())) {
            throw new IllegalArgumentException("Product name already exists: " + input.getName());
        }
        try {
            return toResponse(repo.save(input));
        } catch (DataIntegrityViolationException die) {
            throw new IllegalArgumentException("Product name must be unique", die);
        }
    }

    /** READ **/
    public Page<ProductResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(this::toResponse);
    }

    /** Bro-metode til domænelaget: hent entity eller kast 404/400 */
    public Product getEntityByIdOrThrow(Long id) {
        return repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    /** UPDATE -> DTO **/
    @Transactional
    public ProductResponse update(Long id, ProductRequest req) {
        Product existing = repo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // unikhedscheck hvis navn ændres
        if (req.name() != null && !req.name().equalsIgnoreCase(existing.getName())) {
            if (repo.existsByNameIgnoreCase(req.name())) {
                throw new IllegalArgumentException("Product name already exists: " + req.name());
            }
        }

        apply(existing, req);
        validate(existing);
        return toResponse(repo.save(existing));
    }

    /** DELETE **/
    @Transactional
    public void delete(Long id) {
        Product existing = repo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
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

    // --- mapning ---
    private Product apply(Product t, ProductRequest r) {
        t.setName(r.name());
        t.setPrice(r.price());
        // match entity: getIsActive()/setIsActive(Boolean)
        t.setIsActive(Boolean.TRUE.equals(r.isActive()));
        return t;
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getIsActive());
    }


    /**
     * Hent et produkt som DTO.
     * Lag-grænse: Denne metode er til WEB/API-laget og returnerer en ProductResponse.
     * Domænelogik der har brug for en entity skal bruge getEntityByIdOrThrow(id).
     *
     * @param id produktets id (må ikke være null)
     * @return ProductResponse for det angivne id
     * @throws ProductNotFoundException hvis produktet ikke findes
     */
    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toResponse(p);
    }
}
