package com.example.miniProjekt.Repository;

import com.example.miniProjekt.model.Product;
import com.example.miniProjekt.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// @Repository markerer denne klasse som data access layer
@Repository
// 'interface' betyder at Spring automatisk laver implementeringen
// JpaRepository<Product, Long> betyder:
//   - Product: entitetstypen vi arbejder med
//   - Long: typen på primary key (ID'et)
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Spring forstår navnet "findByCategory" og genererer SQL automatisk
    // Returnerer en liste af produkter med den givne kategori
    List<Product> findByCategory(ProductCategory category);

    // Spring læser "findByStockQuantityGreaterThan" og genererer:
    // Returnerer produkter med lager større end den givne værdi
    List<Product> findByStockQuantityGreaterThan(Integer quantity);
}