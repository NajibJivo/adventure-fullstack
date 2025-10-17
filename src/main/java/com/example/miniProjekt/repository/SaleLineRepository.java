package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.SaleLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SaleLineRepository extends JpaRepository<SaleLine, Long> {
    List<SaleLine> findBySaleId(Long saleId);
    boolean existsBySale_IdAndProduct_Id(Long saleId, Long productId);
    Optional<SaleLine> findBySale_IdAndProduct_Id(Long saleId, Long productId);
    void deleteBySaleId(Long saleId); // <- til delete flowet
}
