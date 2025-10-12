package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Product;
import com.example.miniProjekt.model.Sale;
import com.example.miniProjekt.model.SaleLine;
import com.example.miniProjekt.repository.SaleLineRepository;
import com.example.miniProjekt.repository.SaleRepository;
import com.example.miniProjekt.service.exceptions.SaleNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {
    private final SaleRepository saleRepo;
    private final SaleLineRepository lineRepo;
    private final ProductServiceDto service;

    public SaleService(SaleRepository saleRepo, SaleLineRepository lineRepo, ProductServiceDto service) {
        this.saleRepo = saleRepo;
        this.lineRepo = lineRepo;
        this.service = service;
    }

    /* --- READ helpers --- */
    public Sale getByIdOrThrow(Long saleId) {
        return saleRepo.findById(saleId).orElseThrow(() -> new SaleNotFoundException(saleId));
    }

    public List<SaleLine> listLines(Long saleId) {
        ensureSaleExists(saleId);
        return lineRepo.findBySale_Id(saleId);
    }

    /* --- CREATE sale --- */
    @Transactional
    public Sale createSale(LocalDateTime whenOrNull) {
        Sale s = new Sale();
        s.setSaleDateTime(whenOrNull != null ? whenOrNull : LocalDateTime.now());
        return saleRepo.save(s);
    }

    /* --- ADD line --- */
    @Transactional
    public SaleLine addLine(Long saleId, Long productId, int quantity, BigDecimal unitPriceOrNull) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");

        Sale sale = getByIdOrThrow(saleId);
        Product product = service.getEntityByIdOrThrow(productId);

        if (lineRepo.existsBySale_IdAndProduct_Id(saleId, productId)) {
            throw new DataIntegrityViolationException("Product already in sale (use update or remove first)");
        }

        BigDecimal unitPrice = (unitPriceOrNull != null) ? unitPriceOrNull : product.getPrice();
        if (unitPrice == null) throw new IllegalArgumentException("unitPrice is required (product has no price)");

        SaleLine line = new SaleLine();
        line.setSale(sale);
        line.setProduct(product);
        line.setQuantity(quantity);
        line.setUnitPrice(unitPrice);

        return lineRepo.save(line);
    }

    /* --- REMOVE line --- */
    @Transactional
    public void removeLine(Long saleId, Long productId) {
        ensureSaleExists(saleId);
        lineRepo.deleteBySale_IdAndProduct_Id(saleId, productId);
    }

    /* --- TOTAL --- */
    @Transactional(readOnly = true)
    public BigDecimal getTotal(Long saleId) {
        ensureSaleExists(saleId);
        return lineRepo.findBySale_Id(saleId).stream()
                .map(l -> l.getUnitPrice().multiply(BigDecimal.valueOf(l.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void ensureSaleExists(Long saleId) {
        if (!saleRepo.existsById(saleId)) throw new SaleNotFoundException(saleId);
    }

    @Transactional
    public SaleLine updateLine(Long saleId, Long productId,
                               Integer newQuantity, BigDecimal newUnitPrice) {
        ensureSaleExists(saleId);

        SaleLine line = lineRepo.findBySale_IdAndProduct_Id(saleId, productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sale line not found for saleId=" + saleId + " and productId=" + productId));

        if (newQuantity != null) {
            if (newQuantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
            line.setQuantity(newQuantity);
        }
        if (newUnitPrice != null) {
            if (newUnitPrice.signum() <= 0) throw new IllegalArgumentException("unitPrice must be > 0");
            line.setUnitPrice(newUnitPrice);
        }
        return lineRepo.save(line);
    }
}
