package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.Sale;
import com.example.miniProjekt.model.SaleLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<SaleLine> findBySale_Id(Long saleId);
    boolean existsBySale_IdAndProduct_Id(Long saleId, Long productId);
    void deleteBySale_IdAndProduct_Id(Long saleId, Long productId);
}
