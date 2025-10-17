package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Product;
import com.example.miniProjekt.model.Sale;
import com.example.miniProjekt.model.SaleLine;
import com.example.miniProjekt.repository.CustomerRepository;
import com.example.miniProjekt.repository.ProductRepository;
import com.example.miniProjekt.repository.SaleLineRepository;
import com.example.miniProjekt.repository.SaleRepository;
import com.example.miniProjekt.web.dto.SaleLineRequest;
import com.example.miniProjekt.web.dto.SaleLineItemResponse;
import com.example.miniProjekt.web.dto.SaleRequest;
import com.example.miniProjekt.web.dto.SaleResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {
    private final SaleRepository saleRepo;
    private final SaleLineRepository lineRepo;
    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;

    public SaleService(SaleRepository saleRepo,
                       SaleLineRepository lineRepo,
                       ProductRepository productRepo,
                       CustomerRepository customerRepo) {
        this.saleRepo = saleRepo;
        this.lineRepo = lineRepo;
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
    }

    /** CREATE **/
    @Transactional
    public SaleResponse create(SaleRequest req) {
        validateCreate(req);


        // 1) Opret Sale-head
        Sale sale = new Sale();
        sale.setSaleDateTime(req.saleDateTime());
        sale = saleRepo.save(sale);

        // 2) Opret linjer (historisk unitPrice låses fra Product)
        for (SaleLineRequest li : req.lines()) {
            Product product = productRepo.findById(li.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + li.productId()));

            SaleLine sl = new SaleLine();
            sl.setSale(sale);
            sl.setProduct(product);
            sl.setQuantity(li.quantity());
            sl.setUnitPrice(product.getPrice()); // vigtigt: historisk pris
            lineRepo.save(sl);
        }

        // 3) Hent alt samlet og map til response
        List<SaleLine> persisted = lineRepo.findBySaleId(sale.getId());
        return toResponse(sale, persisted);
    }

    /** READ single **/
    @Transactional(readOnly = true)
    public SaleResponse get(Long id) {
        Sale sale = saleRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found: " + id));
        List<SaleLine> lines = lineRepo.findBySaleId(id);
        return toResponse(sale, lines);
    }


    /** READ all **/
    @Transactional(readOnly = true)
    public List<SaleResponse> list() {
        // For performance kan man lave join fetch/DTO-query; her gør vi det enkelt og tydeligt
        return saleRepo.findAll().stream()
                .map(s -> toResponse(s, lineRepo.findBySaleId(s.getId())))
                .toList();
    }

    // ---------- Mapping ----------

    private SaleResponse toResponse(Sale s, List<SaleLine> lines) {
        List<SaleLineItemResponse> dtoLines = lines.stream()
                .map(this::toLineResponse)
                .toList();

        BigDecimal total = dtoLines.stream()
                .map(SaleLineItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long customerId = s.getCustomer() != null ? s.getCustomer().getId() : null;

        return new SaleResponse(
                s.getId(),
                s.getSaleDateTime(),
                customerId,
                total,
                dtoLines
        );
    }

    private SaleLineItemResponse toLineResponse(SaleLine sl) {
        BigDecimal lineTotal = sl.getUnitPrice().multiply(BigDecimal.valueOf(sl.getQuantity()));
        return new SaleLineItemResponse(
                sl.getId(),
                sl.getProduct().getId(),
                sl.getProduct().getName(),
                sl.getQuantity(),
                sl.getUnitPrice(),
                lineTotal
        );
    }

    // ---------- Validation ----------

    private void validateCreate(SaleRequest req) {
        if (req.saleDateTime() == null) {
            throw new IllegalArgumentException("saleDateTime is required");
        }
        requirePastOrNow(req.saleDateTime(), "saleDateTime"); // salg må gerne være nu/fortid

        if (req.lines() == null || req.lines().isEmpty()) {
            throw new IllegalArgumentException("At least one line is required");
        }
        for (SaleLineRequest li : req.lines()) {
            if (li.productId() == null) {
                throw new IllegalArgumentException("productId is required for each line");
            }
            if (li.quantity() == null || li.quantity() <= 0) {
                throw new IllegalArgumentException("quantity must be > 0");
            }
        }
    }

    private void requirePastOrNow(LocalDateTime dt, String field) {
        if (dt.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(field + " cannot be in the future");
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!saleRepo.existsById(id)) {
            throw new IllegalArgumentException("Sale not found: " + id);
        }
        // Slet linjer først (hvis du ikke har cascade = ALL + orphanRemoval = true på Sale -> lines)
        lineRepo.deleteBySaleId(id);

        // Slet selve salget
        saleRepo.deleteById(id);
    }
}
