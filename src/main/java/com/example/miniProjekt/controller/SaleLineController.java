package com.example.miniProjekt.controller;

import com.example.miniProjekt.service.SaleLineService;
import com.example.miniProjekt.web.dto.SaleLineRequest;
import com.example.miniProjekt.web.dto.SaleLineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


/**
 * REST-API for linjer på et sale.
 * Alle endpoints ligger under /api/sales/{saleId}/lines
 */
@RestController
@RequestMapping("api/sales/{saLeId}/Lines")
public class SaleLineController {
    private final SaleLineService service;

    public SaleLineController(SaleLineService service) {
        this.service = service;
    }

    /** CREATE **/
    @PostMapping
    public ResponseEntity<SaleLineResponse> create(@PathVariable Long saleId,
                                                   @RequestBody SaleLineRequest req) {
        SaleLineResponse created = service.create(saleId, req);
        URI location = URI.create("/api/sales/" + saleId + "/lines/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    /** LIST **/
    @GetMapping
    public List<SaleLineResponse> list(@PathVariable Long saleId) {
        return service.list(saleId);
    }

    /** GET ONE **/
    @GetMapping("/{lineId}")
    public SaleLineResponse get(@PathVariable Long saleId, @PathVariable Long lineId) {
        return service.get(saleId, lineId);
    }

    /** UPDATE **/
    @PutMapping("/{lineId}")
    public SaleLineResponse update(@PathVariable Long saleId,
                                   @PathVariable Long lineId,
                                   @RequestBody SaleLineRequest req) {
        return service.update(saleId, lineId, req);
    }

    /** DELETE **/
    @DeleteMapping("/{lineId}")
    public ResponseEntity<Void> delete(@PathVariable Long saleId, @PathVariable Long lineId) {
        service.delete(saleId, lineId);
        return ResponseEntity.noContent().build();
    }
}
