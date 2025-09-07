package dev.luanfernandes.domain.dto.result;

import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.UserId;

public record TopUserReport(UserId userId, String userName, Money totalSpent, int orderCount) {}
