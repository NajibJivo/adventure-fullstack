package com.example.miniProjekt.web.mapper;

import com.example.miniProjekt.model.Product;
import com.example.miniProjekt.web.dto.ProductRequest;
import com.example.miniProjekt.web.dto.ProductResponse;

public class ProductDtoMapper {
    private ProductDtoMapper() {}

    public static Product toEntity(ProductRequest r) {
        Product p = new Product();
        copy(r, p);
        return p;
    }

    public static void copy(ProductRequest r, Product target) {
        target.setName(r.name());
        target.setPrice(r.price());
        target.setIsActive(r.isActive() == null ? Boolean.TRUE : r.isActive());
    }

    public static ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getPrice(),
                p.getIsActive()
        );
    }
}
