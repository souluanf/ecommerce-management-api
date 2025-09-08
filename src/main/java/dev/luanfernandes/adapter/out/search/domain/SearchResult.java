package dev.luanfernandes.adapter.out.search.domain;

import dev.luanfernandes.adapter.out.search.document.ProductDocument;
import java.util.List;

public record SearchResult(
        List<ProductDocument> products,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious) {
    public static SearchResult of(
            List<ProductDocument> products, long totalElements, int totalPages, int currentPage, int pageSize) {
        return new SearchResult(
                products,
                totalElements,
                totalPages,
                currentPage,
                pageSize,
                currentPage < totalPages - 1,
                currentPage > 0);
    }

    public static SearchResult empty() {
        return new SearchResult(List.of(), 0, 0, 0, 20, false, false);
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    public int size() {
        return products.size();
    }
}
