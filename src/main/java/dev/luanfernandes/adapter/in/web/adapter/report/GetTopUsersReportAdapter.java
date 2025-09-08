package dev.luanfernandes.adapter.in.web.adapter.report;

import dev.luanfernandes.adapter.in.web.port.report.GetTopUsersReportPort;
import dev.luanfernandes.application.usecase.report.GenerateTopUsersReportUseCase;
import dev.luanfernandes.domain.dto.TopUserReportResponse;
import dev.luanfernandes.domain.dto.query.TopUsersQuery;
import dev.luanfernandes.domain.dto.result.TopUserReport;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GetTopUsersReportAdapter implements GetTopUsersReportPort {

    private final GenerateTopUsersReportUseCase generateTopUsersReportUseCase;

    @Override
    public ResponseEntity<List<TopUserReportResponse>> getTopUsers(LocalDate startDate, LocalDate endDate, int limit) {
        log.info("Getting top users report: startDate={}, endDate={}, limit={}", startDate, endDate, limit);

        TopUsersQuery query = new TopUsersQuery(startDate, endDate, limit);
        List<TopUserReport> topUsers = generateTopUsersReportUseCase.execute(query);

        List<TopUserReportResponse> response = topUsers.stream()
                .map(user -> new TopUserReportResponse(
                        user.userId().value(),
                        user.userName(),
                        user.totalSpent().value(),
                        user.orderCount()))
                .toList();

        log.info("Returning {} top users", response.size());
        return ResponseEntity.ok(response);
    }
}
