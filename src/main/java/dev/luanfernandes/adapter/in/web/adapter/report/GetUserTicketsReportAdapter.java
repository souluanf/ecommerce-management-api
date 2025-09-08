package dev.luanfernandes.adapter.in.web.adapter.report;

import dev.luanfernandes.adapter.in.web.port.report.GetUserTicketsReportPort;
import dev.luanfernandes.application.usecase.report.GenerateUserTicketAverageReportUseCase;
import dev.luanfernandes.domain.dto.UserTicketReportResponse;
import dev.luanfernandes.domain.dto.query.UserTicketQuery;
import dev.luanfernandes.domain.dto.result.UserTicketReport;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GetUserTicketsReportAdapter implements GetUserTicketsReportPort {

    private final GenerateUserTicketAverageReportUseCase generateUserTicketAverageReportUseCase;

    @Override
    public ResponseEntity<List<UserTicketReportResponse>> getUserTickets(LocalDate startDate, LocalDate endDate) {
        log.info("Getting user ticket average report: startDate={}, endDate={}", startDate, endDate);

        UserTicketQuery query = new UserTicketQuery(startDate, endDate);
        List<UserTicketReport> userTickets = generateUserTicketAverageReportUseCase.execute(query);

        List<UserTicketReportResponse> response = userTickets.stream()
                .map(ticket -> new UserTicketReportResponse(
                        ticket.userId().value(), ticket.userName(), ticket.averageTicketValue()))
                .toList();

        log.info("Returning {} user ticket averages", response.size());
        return ResponseEntity.ok(response);
    }
}
