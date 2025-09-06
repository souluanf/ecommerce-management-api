package dev.luanfernandes.domain.dto;

import dev.luanfernandes.domain.entity.ProductDomain;
import java.util.List;

public record ProductListResponse(List<ProductResponse> products) {
    public static ProductListResponse from(List<ProductDomain> products) {
        var productResponses = products.stream().map(ProductResponse::from).toList();
        return new ProductListResponse(productResponses);
    }
}
