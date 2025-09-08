package dev.luanfernandes.domain.dto;

import java.math.BigDecimal;

public record AdvancedSearchRequest(
        String query,
        String category,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        int page,
        int size,
        String sortField,
        String sortDirection) {}
