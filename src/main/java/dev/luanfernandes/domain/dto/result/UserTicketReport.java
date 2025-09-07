package dev.luanfernandes.domain.dto.result;

import dev.luanfernandes.domain.valueobject.UserId;
import java.math.BigDecimal;

public record UserTicketReport(UserId userId, String userName, BigDecimal averageTicketValue) {}
