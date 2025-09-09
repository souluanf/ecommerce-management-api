package dev.luanfernandes.application.usecase.report;

import dev.luanfernandes.domain.dto.query.UserTicketQuery;
import dev.luanfernandes.domain.dto.result.UserTicketReport;
import dev.luanfernandes.domain.exception.InvalidParameterException;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenerateUserTicketAverageReportUseCase {

    private final OrderRepository orderRepository;

    public List<UserTicketReport> execute(UserTicketQuery query) {
        log.info("Generating user ticket average report with query: {}", query);

        validateUserTicketQuery(query);

        LocalDateTime startDateTime =
                query.startDate() != null ? query.startDate().atStartOfDay() : null;
        LocalDateTime endDateTime = query.endDate() != null ? query.endDate().atTime(23, 59, 59) : null;

        List<UserTicketReport> report = orderRepository.findUserTicketAverage(startDateTime, endDateTime);

        log.info("Generated user ticket average report with {} users", report.size());
        return report;
    }

    private void validateUserTicketQuery(UserTicketQuery query) {
        Objects.requireNonNull(query, "Query cannot be null");
        validateDateRange(query.startDate(), query.endDate());
    }

    private void validateDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidParameterException("Start date must be before or equal to end date");
        }

        java.time.LocalDate tomorrow = java.time.LocalDate.now().plusDays(1);
        if (startDate != null && startDate.isAfter(tomorrow)) {
            throw new InvalidParameterException("Start date cannot be in the future");
        }

        if (endDate != null && endDate.isAfter(tomorrow)) {
            throw new InvalidParameterException("End date cannot be in the future");
        }
    }
}
