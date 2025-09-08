package dev.luanfernandes.adapter.in.web.adapter.report;

import dev.luanfernandes.adapter.in.web.port.report.GetMonthlyRevenueReportPort;
import dev.luanfernandes.application.usecase.report.GetCurrentMonthRevenueUseCase;
import dev.luanfernandes.domain.dto.MonthlyRevenueResponse;
import dev.luanfernandes.domain.valueobject.Money;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GetMonthlyRevenueReportAdapter implements GetMonthlyRevenueReportPort {

    private final GetCurrentMonthRevenueUseCase getCurrentMonthRevenueUseCase;

    @Override
    public ResponseEntity<MonthlyRevenueResponse> getMonthlyRevenue() {
        log.info("Getting current month revenue report");

        Money revenue = getCurrentMonthRevenueUseCase.execute();
        LocalDateTime now = LocalDateTime.now();

        MonthlyRevenueResponse response =
                new MonthlyRevenueResponse(now.getYear(), now.getMonthValue(), revenue.value());

        log.info("Current month revenue: {}", revenue.value());
        return ResponseEntity.ok(response);
    }
}
