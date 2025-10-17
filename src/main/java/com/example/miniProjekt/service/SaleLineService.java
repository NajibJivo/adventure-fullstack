package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Product;
import com.example.miniProjekt.model.Sale;
import com.example.miniProjekt.model.SaleLine;
import com.example.miniProjekt.repository.ProductRepository;
import com.example.miniProjekt.repository.SaleLineRepository;
import com.example.miniProjekt.repository.SaleRepository;
import com.example.miniProjekt.web.dto.SaleLineRequest;
import com.example.miniProjekt.web.dto.SaleLineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Forretningslogik for salgs-linjer (SaleLine).
 * Holder web-laget uafhængigt via DTO’er.
 */
@Service
public class SaleLineService {
    private final SaleLineRepository repo;
    private final SaleRepository saleRepo;
    private final ProductRepository productRepo;

    public SaleLineService(SaleLineRepository repo, SaleRepository saleRepo, ProductRepository productRepo) {
        this.repo = repo;
        this.saleRepo = saleRepo;
        this.productRepo = productRepo;
    }

    /**
     * Opret en linje på et eksisterende sale.
     */
    @Transactional
    public SaleLineResponse create(Long saleId, SaleLineRequest req) {
        Sale sale = saleRepo.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found: " + saleId));

        Product product = productRepo.findById(req.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + req.productId()));

        int qty = requirePositive(req.quantity(), "quantity");

        BigDecimal price = req.unitPrice() != null ? req.unitPrice() : product.getPrice();
        requireNonNegative(price, "unitPrice");

        SaleLine line = new SaleLine();
        line.setSale(sale);
        line.setProduct(product);
        line.setQuantity(qty);
        line.setUnitPrice(price);

        return toResponse(repo.save(line));
    }

    /**
     * Hent alle linjer til et sale.
     */
    @Transactional(readOnly = true)
    public List<SaleLineResponse> list(Long saleId) {
        // (valgfrit) check at sale findes, ellers returner bare tom liste hvis du vil
        assertSaleExists(saleId);
        return repo.findBySaleId(saleId).stream().map(this::toResponse).toList();
    }

    /**
     * Hent en specifik linje og bekræft at den tilhører saleId.
     */
    @Transactional(readOnly = true)
    public SaleLineResponse get(Long saleId, Long lineId) {
        SaleLine line = requireLineInSale(saleId, lineId);
        return toResponse(line);
    }

    /**
     * Opdater en linje (quantity, evtl. unitPrice, evtl. product).
     */
    @Transactional
    public SaleLineResponse update(Long saleId, Long lineId, SaleLineRequest req) {
        SaleLine line = requireLineInSale(saleId, lineId);

        if (req.productId() != null && !req.productId().equals(line.getProduct().getId())) {
            Product product = productRepo.findById(req.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + req.productId()));
            line.setProduct(product);
            // hvis unitPrice ikke er angivet i req, så opdater til produktets nuværende pris
            if (req.unitPrice() == null) {
                line.setUnitPrice(product.getPrice());
            }
        }

        if (req.quantity() != null) {
            line.setQuantity(requirePositive(req.quantity(), "quantity"));
        }

        if (req.unitPrice() != null) {
            requireNonNegative(req.unitPrice(), "unitPrice");
            line.setUnitPrice(req.unitPrice());
        }

        return toResponse(repo.save(line));
    }

    /**
     * Slet en linje fra et sale.
     */
    @Transactional
    public void delete(Long saleId, Long lineId) {
        SaleLine line = requireLineInSale(saleId, lineId);
        repo.delete(line);
    }

    // ---------- Helpers ----------

    private void assertSaleExists(Long saleId) {
        if (!saleRepo.existsById(saleId)) {
            throw new IllegalArgumentException("Sale not found: " + saleId);
        }
    }

    private SaleLine requireLineInSale(Long saleId, Long lineId) {
        SaleLine line = repo.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("SaleLine not found: " + lineId));
        if (!line.getSale().getId().equals(saleId)) {
            throw new IllegalArgumentException("SaleLine " + lineId + " does not belong to sale " + saleId);
        }
        return line;
    }

    private int requirePositive(Integer val, String field) {
        if (val == null || val <= 0) {
            throw new IllegalArgumentException(field + " must be > 0");
        }
        return val;
    }

    private void requireNonNegative(BigDecimal val, String field) {
        if (val == null || val.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(field + " must be >= 0");
        }
    }

    private SaleLineResponse toResponse(SaleLine l) {
        BigDecimal total = l.getUnitPrice().multiply(BigDecimal.valueOf(l.getQuantity()));
        return new SaleLineResponse(
                l.getId(),
                l.getSale().getId(),
                l.getProduct().getId(),
                l.getProduct().getName(),
                l.getQuantity(),
                l.getUnitPrice(),
                total
        );
    }
}
