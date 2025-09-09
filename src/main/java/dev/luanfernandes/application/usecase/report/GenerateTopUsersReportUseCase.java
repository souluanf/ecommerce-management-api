package dev.luanfernandes.application.usecase.report;

import dev.luanfernandes.domain.dto.query.TopUsersQuery;
import dev.luanfernandes.domain.dto.result.TopUserReport;
import dev.luanfernandes.domain.exception.InvalidParameterException;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import java.time.LocalDate;
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
public class GenerateTopUsersReportUseCase {

    private final OrderRepository orderRepository;

    public List<TopUserReport> execute(TopUsersQuery query) {
        log.info("Generating top users report with query: {}", query);

        validateTopUsersQuery(query);

        LocalDateTime startDateTime =
                query.startDate() != null ? query.startDate().atStartOfDay() : null;
        LocalDateTime endDateTime = query.endDate() != null ? query.endDate().atTime(23, 59, 59) : null;

        List<TopUserReport> report =
                orderRepository.findTopUsersByTotalSpent(startDateTime, endDateTime, query.limit());

        log.info("Generated top users report with {} users", report.size());
        return report;
    }

    private void validateTopUsersQuery(TopUsersQuery query) {
        Objects.requireNonNull(query, "Query cannot be null");

        if (query.limit() < 1 || query.limit() > 100) {
            throw new InvalidParameterException("Limit must be between 1 and 100");
        }

        validateDateRange(query.startDate(), query.endDate());
    }

    private void validateDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidParameterException("Start date must be before or equal to end date");
        }

        LocalDate tomorrow = java.time.LocalDate.now().plusDays(1);
        if (startDate != null && startDate.isAfter(tomorrow)) {
            throw new InvalidParameterException("Start date cannot be in the future");
        }

        if (endDate != null && endDate.isAfter(tomorrow)) {
            throw new InvalidParameterException("End date cannot be in the future");
        }
    }
}
