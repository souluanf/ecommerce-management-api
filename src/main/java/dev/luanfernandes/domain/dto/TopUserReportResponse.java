package dev.luanfernandes.domain.dto;

import java.math.BigDecimal;

public record TopUserReportResponse(String userId, String email, BigDecimal totalSpent, Integer orderCount) {}
