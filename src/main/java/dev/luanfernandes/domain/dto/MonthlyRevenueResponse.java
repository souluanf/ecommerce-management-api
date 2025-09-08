package dev.luanfernandes.domain.dto;

import java.math.BigDecimal;

public record MonthlyRevenueResponse(Integer year, Integer month, BigDecimal totalRevenue) {}
