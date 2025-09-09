package dev.luanfernandes.application.usecase.report;

import dev.luanfernandes.domain.port.out.order.OrderRepository;
import dev.luanfernandes.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCurrentMonthRevenueUseCase {

    private final OrderRepository orderRepository;

    public Money execute() {
        log.info("Generating current month revenue report");

        LocalDate now = LocalDate.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);

        BigDecimal revenue = orderRepository.calculateMonthlyRevenue(startOfMonth, endOfMonth);

        Money totalRevenue = Money.of(revenue != null ? revenue : BigDecimal.ZERO);
        log.info("Current month revenue: {}", totalRevenue);

        return totalRevenue;
    }
}
