package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.Sale;
import com.example.miniProjekt.model.SaleLine;
import com.example.miniProjekt.service.SaleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sales")
public class SaleController {
    private final SaleService service;

    public SaleController(SaleService service) {
        this.service = service;
    }

    /** Create sale (201 + Location). Optional: ?when=2025-10-10T12:00:00 */
    @PostMapping
    public ResponseEntity<Sale> create(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime when) {

        Sale saved = service.createSale(when);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    /** Add line: { "productId": 1, "quantity": 2, "unitPrice": 19.95 (optional) } */
    @PostMapping("/{saleId}/lines")
    public ResponseEntity<SaleLine> addLine(@PathVariable Long saleId,
                                            @RequestBody AddLineRequest req) {
        SaleLine line = service.addLine(saleId, req.productId(), req.quantity(), req.unitPrice());
        return ResponseEntity.ok(line);
    }

    /** List lines for sale */
    @GetMapping("/{saleId}/lines")
    public List<SaleLine> listLines(@PathVariable Long saleId) {
        return service.listLines(saleId);
    }

    /** Remove specific product from sale */
    @DeleteMapping("/{saleId}/lines/{productId}")
    public ResponseEntity<Void> removeLine(@PathVariable Long saleId,
                                           @PathVariable Long productId) {
        service.removeLine(saleId, productId);
        return ResponseEntity.noContent().build();
    }

    /** Total for sale */
    @GetMapping("/{saleId}/total")
    public BigDecimal getTotal(@PathVariable Long saleId) {
        return service.getTotal(saleId);
    }

    /** Request DTO used only for the addLine endpoint */
    public record AddLineRequest(Long productId, int quantity, BigDecimal unitPrice) {}


    @PutMapping("/{saleId}/lines/{productId}")
    public ResponseEntity<SaleLine> updateLine(@PathVariable Long saleId,
                                               @PathVariable Long productId,
                                               @RequestBody UpdateLineRequest req) {
        SaleLine line = service.updateLine(saleId, productId, req.quantity(), req.unitPrice());
        return ResponseEntity.ok(line);
    }

    /** Request DTO for update */
    public record UpdateLineRequest(Integer quantity, BigDecimal unitPrice) {}

}
