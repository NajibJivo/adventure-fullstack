package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.SaleLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


/**
 * Repo for sale lines. Query-navne matcher FK-felter.
 */
public interface SaleLineRepository extends JpaRepository<SaleLine, Long> {

    /**
     * Find alle linjer til et givent sale.
     */
    List<SaleLine> findBySaleId(Long saleId);


    void deleteBySaleId(Long saleId); // <- til delete flowet
}
