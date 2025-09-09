package dev.luanfernandes.adapter.out.search.domain;

import java.math.BigDecimal;

public record SearchCriteria(
        String query,
        String category,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        int page,
        int size,
        String sortField,
        String sortDirection) {
    public static SearchCriteria of(String query) {
        return new SearchCriteria(query, null, null, null, 0, 20, null, null);
    }

    public static SearchCriteria of(String query, int page, int size) {
        return new SearchCriteria(query, null, null, null, page, size, null, null);
    }
}
