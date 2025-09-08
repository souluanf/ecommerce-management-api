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

    public SearchCriteria withCategory(String category) {
        return new SearchCriteria(query, category, minPrice, maxPrice, page, size, sortField, sortDirection);
    }

    public SearchCriteria withPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return new SearchCriteria(query, category, minPrice, maxPrice, page, size, sortField, sortDirection);
    }

    public SearchCriteria withSort(String sortField, String sortDirection) {
        return new SearchCriteria(query, category, minPrice, maxPrice, page, size, sortField, sortDirection);
    }

    public boolean hasQuery() {
        return query != null && !query.trim().isEmpty();
    }

    public boolean hasCategory() {
        return category != null && !category.trim().isEmpty();
    }

    public boolean hasPriceRange() {
        return minPrice != null || maxPrice != null;
    }

    public boolean hasSort() {
        return sortField != null && !sortField.trim().isEmpty();
    }
}
