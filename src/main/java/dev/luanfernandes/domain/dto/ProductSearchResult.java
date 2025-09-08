package dev.luanfernandes.domain.dto;

import dev.luanfernandes.domain.entity.ProductDomain;
import java.util.List;

public record ProductSearchResult(
        List<ProductDomain> products,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious) {

    public static ProductSearchResult of(
            List<ProductDomain> products, long totalElements, int totalPages, int currentPage, int pageSize) {
        return new ProductSearchResult(
                products,
                totalElements,
                totalPages,
                currentPage,
                pageSize,
                currentPage < totalPages - 1,
                currentPage > 0);
    }

    public static ProductSearchResult empty() {
        return new ProductSearchResult(List.of(), 0, 0, 0, 20, false, false);
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    public int size() {
        return products.size();
    }
}
