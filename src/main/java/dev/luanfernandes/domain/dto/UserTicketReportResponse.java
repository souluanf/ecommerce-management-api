package dev.luanfernandes.domain.dto;

import java.math.BigDecimal;

public record UserTicketReportResponse(String userId, String email, BigDecimal averageTicket) {}
