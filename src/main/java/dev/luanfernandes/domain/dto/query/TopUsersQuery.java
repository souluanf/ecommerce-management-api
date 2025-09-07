package dev.luanfernandes.domain.dto.query;

import java.time.LocalDate;

public record TopUsersQuery(LocalDate startDate, LocalDate endDate, int limit) {}
