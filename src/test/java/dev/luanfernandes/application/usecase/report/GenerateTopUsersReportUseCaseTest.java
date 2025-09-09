package dev.luanfernandes.application.usecase.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.dto.query.TopUsersQuery;
import dev.luanfernandes.domain.dto.result.TopUserReport;
import dev.luanfernandes.domain.exception.InvalidParameterException;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.UserId;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for GenerateTopUsersReportUseCase")
class GenerateTopUsersReportUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private GenerateTopUsersReportUseCase generateTopUsersReportUseCase;

    @Test
    @DisplayName("Should generate top users report with valid query")
    void shouldGenerateTopUsersReport_WithValidQuery() {
        TopUsersQuery query = new TopUsersQuery(LocalDate.now().minusDays(30), LocalDate.now(), 10);
        List<TopUserReport> expectedReport = Arrays.asList(
                createTopUserReport("user1@example.com", new BigDecimal("1000.00"), 5),
                createTopUserReport("user2@example.com", new BigDecimal("750.50"), 3),
                createTopUserReport("user3@example.com", new BigDecimal("500.25"), 2));

        when(orderRepository.findTopUsersByTotalSpent(any(LocalDateTime.class), any(LocalDateTime.class), eq(10)))
                .thenReturn(expectedReport);

        List<TopUserReport> result = generateTopUsersReportUseCase.execute(query);

        assertThat(result).hasSize(3).containsExactlyElementsOf(expectedReport);
        assertThat(result.getFirst().userName()).isEqualTo("user1@example.com");
        assertThat(result.getFirst().totalSpent()).isEqualTo(new Money(new BigDecimal("1000.00")));
        assertThat(result.getFirst().orderCount()).isEqualTo(5);

        verify(orderRepository).findTopUsersByTotalSpent(any(LocalDateTime.class), any(LocalDateTime.class), eq(10));
    }

    @Test
    @DisplayName("Should generate report with null start date")
    void shouldGenerateReport_WithNullStartDate() {
        TopUsersQuery query = new TopUsersQuery(null, LocalDate.now(), 5);
        List<TopUserReport> expectedReport =
                Collections.singletonList(createTopUserReport("user@example.com", new BigDecimal("500.00"), 2));

        when(orderRepository.findTopUsersByTotalSpent(isNull(), any(LocalDateTime.class), eq(5)))
                .thenReturn(expectedReport);

        List<TopUserReport> result = generateTopUsersReportUseCase.execute(query);

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(TopUserReport::userName)
                .isEqualTo("user@example.com");

        verify(orderRepository).findTopUsersByTotalSpent(isNull(), any(LocalDateTime.class), eq(5));
    }

    @Test
    @DisplayName("Should generate report with null end date")
    void shouldGenerateReport_WithNullEndDate() {
        TopUsersQuery query = new TopUsersQuery(LocalDate.now().minusDays(30), null, 3);
        List<TopUserReport> expectedReport = Arrays.asList(
                createTopUserReport("user1@example.com", new BigDecimal("800.00"), 4),
                createTopUserReport("user2@example.com", new BigDecimal("600.00"), 3));

        when(orderRepository.findTopUsersByTotalSpent(any(LocalDateTime.class), isNull(), eq(3)))
                .thenReturn(expectedReport);

        List<TopUserReport> result = generateTopUsersReportUseCase.execute(query);

        assertThat(result).hasSize(2);

        verify(orderRepository).findTopUsersByTotalSpent(any(LocalDateTime.class), isNull(), eq(3));
    }

    @Test
    @DisplayName("Should generate report with both dates null")
    void shouldGenerateReport_WithBothDatesNull() {
        TopUsersQuery query = new TopUsersQuery(null, null, 5);
        List<TopUserReport> expectedReport = Arrays.asList(
                createTopUserReport("user1@example.com", new BigDecimal("1200.00"), 6),
                createTopUserReport("user2@example.com", new BigDecimal("900.00"), 4),
                createTopUserReport("user3@example.com", new BigDecimal("700.00"), 3),
                createTopUserReport("user4@example.com", new BigDecimal("500.00"), 2),
                createTopUserReport("user5@example.com", new BigDecimal("300.00"), 1));

        when(orderRepository.findTopUsersByTotalSpent(isNull(), isNull(), eq(5)))
                .thenReturn(expectedReport);

        List<TopUserReport> result = generateTopUsersReportUseCase.execute(query);

        assertThat(result).hasSize(5);

        verify(orderRepository).findTopUsersByTotalSpent(isNull(), isNull(), eq(5));
    }

    @Test
    @DisplayName("Should return empty list when no users found")
    void shouldReturnEmptyList_WhenNoUsersFound() {
        TopUsersQuery query = new TopUsersQuery(LocalDate.now().minusDays(7), LocalDate.now(), 10);

        when(orderRepository.findTopUsersByTotalSpent(any(LocalDateTime.class), any(LocalDateTime.class), eq(10)))
                .thenReturn(Collections.emptyList());

        List<TopUserReport> result = generateTopUsersReportUseCase.execute(query);

        assertThat(result).isEmpty();

        verify(orderRepository).findTopUsersByTotalSpent(any(LocalDateTime.class), any(LocalDateTime.class), eq(10));
    }

    @Test
    @DisplayName("Should generate report with limit 1")
    void shouldGenerateReport_WithLimit1() {
        TopUsersQuery query = new TopUsersQuery(LocalDate.now().minusDays(30), LocalDate.now(), 1);
        List<TopUserReport> expectedReport =
                Collections.singletonList(createTopUserReport("topuser@example.com", new BigDecimal("2000.00"), 10));

        when(orderRepository.findTopUsersByTotalSpent(any(LocalDateTime.class), any(LocalDateTime.class), eq(1)))
                .thenReturn(expectedReport);

        List<TopUserReport> result = generateTopUsersReportUseCase.execute(query);

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(TopUserReport::userName)
                .isEqualTo("topuser@example.com");

        verify(orderRepository).findTopUsersByTotalSpent(any(LocalDateTime.class), any(LocalDateTime.class), eq(1));
    }

    @Test
    @DisplayName("Should generate report with limit 100")
    void shouldGenerateReport_WithLimit100() {
        TopUsersQuery query = new TopUsersQuery(LocalDate.now().minusDays(365), LocalDate.now(), 100);
        List<TopUserReport> expectedReport = Arrays.asList(
                createTopUserReport("user1@example.com", new BigDecimal("5000.00"), 25),
                createTopUserReport("user2@example.com", new BigDecimal("4000.00"), 20));

        when(orderRepository.findTopUsersByTotalSpent(any(LocalDateTime.class), any(LocalDateTime.class), eq(100)))
                .thenReturn(expectedReport);

        List<TopUserReport> result = generateTopUsersReportUseCase.execute(query);

        assertThat(result).hasSize(2);

        verify(orderRepository).findTopUsersByTotalSpent(any(LocalDateTime.class), any(LocalDateTime.class), eq(100));
    }

    @Test
    @DisplayName("Should throw exception when query is null")
    void shouldThrowException_WhenQueryIsNull() {
        assertThatThrownBy(() -> generateTopUsersReportUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Query cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when limit is zero")
    void shouldThrowException_WhenLimitIsZero() {
        TopUsersQuery query = new TopUsersQuery(LocalDate.now().minusDays(30), LocalDate.now(), 0);

        assertThatThrownBy(() -> generateTopUsersReportUseCase.execute(query))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("Limit must be between 1 and 100");
    }

    @Test
    @DisplayName("Should throw exception when limit is negative")
    void shouldThrowException_WhenLimitIsNegative() {
        TopUsersQuery query = new TopUsersQuery(LocalDate.now().minusDays(30), LocalDate.now(), -5);

        assertThatThrownBy(() -> generateTopUsersReportUseCase.execute(query))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("Limit must be between 1 and 100");
    }

    @Test
    @DisplayName("Should throw exception when limit exceeds 100")
    void shouldThrowException_WhenLimitExceeds100() {
        TopUsersQuery query = new TopUsersQuery(LocalDate.now().minusDays(30), LocalDate.now(), 101);

        assertThatThrownBy(() -> generateTopUsersReportUseCase.execute(query))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("Limit must be between 1 and 100");
    }

    @Test
    @DisplayName("Should throw exception when start date is after end date")
    void shouldThrowException_WhenStartDateIsAfterEndDate() {
        TopUsersQuery query = new TopUsersQuery(LocalDate.now(), LocalDate.now().minusDays(1), 10);

        assertThatThrownBy(() -> generateTopUsersReportUseCase.execute(query))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("Start date must be before or equal to end date");
    }

    @Test
    @DisplayName("Should throw exception when start date is in the future")
    void shouldThrowException_WhenStartDateIsInTheFuture() {
        TopUsersQuery query =
                new TopUsersQuery(LocalDate.now().plusDays(2), LocalDate.now().plusDays(3), 10);

        assertThatThrownBy(() -> generateTopUsersReportUseCase.execute(query))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("Start date cannot be in the future");
    }

    @Test
    @DisplayName("Should throw exception when end date is in the future")
    void shouldThrowException_WhenEndDateIsInTheFuture() {
        TopUsersQuery query =
                new TopUsersQuery(LocalDate.now().minusDays(1), LocalDate.now().plusDays(2), 10);

        assertThatThrownBy(() -> generateTopUsersReportUseCase.execute(query))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("End date cannot be in the future");
    }

    @Test
    @DisplayName("Should accept start and end date as today")
    void shouldAcceptStartAndEndDate_AsToday() {
        TopUsersQuery query = new TopUsersQuery(LocalDate.now(), LocalDate.now(), 5);
        List<TopUserReport> expectedReport =
                Collections.singletonList(createTopUserReport("user@example.com", new BigDecimal("250.00"), 1));

        when(orderRepository.findTopUsersByTotalSpent(any(LocalDateTime.class), any(LocalDateTime.class), eq(5)))
                .thenReturn(expectedReport);

        List<TopUserReport> result = generateTopUsersReportUseCase.execute(query);

        assertThat(result).hasSize(1);

        verify(orderRepository).findTopUsersByTotalSpent(any(LocalDateTime.class), any(LocalDateTime.class), eq(5));
    }

    @Test
    @DisplayName("Should handle users with zero total spent")
    void shouldHandleUsers_WithZeroTotalSpent() {
        TopUsersQuery query = new TopUsersQuery(LocalDate.now().minusDays(7), LocalDate.now(), 10);
        List<TopUserReport> expectedReport = Arrays.asList(
                createTopUserReport("user1@example.com", new BigDecimal("100.00"), 2),
                createTopUserReport("user2@example.com", BigDecimal.ZERO, 0));

        when(orderRepository.findTopUsersByTotalSpent(any(LocalDateTime.class), any(LocalDateTime.class), eq(10)))
                .thenReturn(expectedReport);

        List<TopUserReport> result = generateTopUsersReportUseCase.execute(query);

        assertThat(result).hasSize(2);
        assertThat(result.get(1))
                .extracting(TopUserReport::totalSpent, TopUserReport::orderCount)
                .containsExactly(new Money(BigDecimal.ZERO), 0);

        verify(orderRepository).findTopUsersByTotalSpent(any(LocalDateTime.class), any(LocalDateTime.class), eq(10));
    }

    private TopUserReport createTopUserReport(String email, BigDecimal totalSpent, int orderCount) {
        return new TopUserReport(UserId.generate(), email, new Money(totalSpent), orderCount);
    }
}
