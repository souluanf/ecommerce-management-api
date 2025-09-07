package dev.luanfernandes.domain.dto.query;

import java.time.LocalDate;

public record UserTicketQuery(LocalDate startDate, LocalDate endDate) {}
