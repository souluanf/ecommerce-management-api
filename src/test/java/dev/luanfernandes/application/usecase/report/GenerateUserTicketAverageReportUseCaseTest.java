package dev.luanfernandes.application.usecase.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.dto.query.UserTicketQuery;
import dev.luanfernandes.domain.dto.result.UserTicketReport;
import dev.luanfernandes.domain.exception.InvalidParameterException;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
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
@DisplayName("Tests for GenerateUserTicketAverageReportUseCase")
class GenerateUserTicketAverageReportUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private GenerateUserTicketAverageReportUseCase generateUserTicketAverageReportUseCase;

    @Test
    @DisplayName("Should generate user ticket average report with valid query")
    void shouldGenerateUserTicketAverageReport_WithValidQuery() {
        UserTicketQuery query = new UserTicketQuery(LocalDate.now().minusDays(30), LocalDate.now());
        List<UserTicketReport> expectedReport = Arrays.asList(
                createUserTicketReport("user1@example.com", new BigDecimal("150.00")),
                createUserTicketReport("user2@example.com", new BigDecimal("200.00")));

        when(orderRepository.findUserTicketAverage(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedReport);

        List<UserTicketReport> result = generateUserTicketAverageReportUseCase.execute(query);

        assertThat(result).hasSize(2).containsExactlyElementsOf(expectedReport);
        assertThat(result.getFirst().userName()).isEqualTo("user1@example.com");
        assertThat(result.getFirst().averageTicketValue()).isEqualTo(new BigDecimal("150.00"));

        verify(orderRepository).findUserTicketAverage(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should generate report with null start date")
    void shouldGenerateReport_WithNullStartDate() {
        UserTicketQuery query = new UserTicketQuery(null, LocalDate.now());
        List<UserTicketReport> expectedReport =
                Collections.singletonList(createUserTicketReport("user@example.com", new BigDecimal("100.00")));

        when(orderRepository.findUserTicketAverage(isNull(), any(LocalDateTime.class)))
                .thenReturn(expectedReport);

        List<UserTicketReport> result = generateUserTicketAverageReportUseCase.execute(query);

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(UserTicketReport::userName)
                .isEqualTo("user@example.com");

        verify(orderRepository).findUserTicketAverage(isNull(), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should generate report with null end date")
    void shouldGenerateReport_WithNullEndDate() {
        UserTicketQuery query = new UserTicketQuery(LocalDate.now().minusDays(30), null);
        List<UserTicketReport> expectedReport =
                Collections.singletonList(createUserTicketReport("user@example.com", new BigDecimal("75.50")));

        when(orderRepository.findUserTicketAverage(any(LocalDateTime.class), isNull()))
                .thenReturn(expectedReport);

        List<UserTicketReport> result = generateUserTicketAverageReportUseCase.execute(query);

        assertThat(result).hasSize(1);

        verify(orderRepository).findUserTicketAverage(any(LocalDateTime.class), isNull());
    }

    @Test
    @DisplayName("Should generate report with both dates null")
    void shouldGenerateReport_WithBothDatesNull() {
        UserTicketQuery query = new UserTicketQuery(null, null);
        List<UserTicketReport> expectedReport = Arrays.asList(
                createUserTicketReport("user1@example.com", new BigDecimal("120.00")),
                createUserTicketReport("user2@example.com", new BigDecimal("180.00")),
                createUserTicketReport("user3@example.com", new BigDecimal("90.00")));

        when(orderRepository.findUserTicketAverage(isNull(), isNull())).thenReturn(expectedReport);

        List<UserTicketReport> result = generateUserTicketAverageReportUseCase.execute(query);

        assertThat(result).hasSize(3);

        verify(orderRepository).findUserTicketAverage(isNull(), isNull());
    }

    @Test
    @DisplayName("Should return empty list when no users found")
    void shouldReturnEmptyList_WhenNoUsersFound() {
        UserTicketQuery query = new UserTicketQuery(LocalDate.now().minusDays(7), LocalDate.now());

        when(orderRepository.findUserTicketAverage(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        List<UserTicketReport> result = generateUserTicketAverageReportUseCase.execute(query);

        assertThat(result).isEmpty();

        verify(orderRepository).findUserTicketAverage(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should throw exception when query is null")
    void shouldThrowException_WhenQueryIsNull() {
        assertThatThrownBy(() -> generateUserTicketAverageReportUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Query cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when start date is after end date")
    void shouldThrowException_WhenStartDateIsAfterEndDate() {
        UserTicketQuery query =
                new UserTicketQuery(LocalDate.now(), LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> generateUserTicketAverageReportUseCase.execute(query))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("Start date must be before or equal to end date");
    }

    @Test
    @DisplayName("Should throw exception when start date is in the future")
    void shouldThrowException_WhenStartDateIsInTheFuture() {
        UserTicketQuery query =
                new UserTicketQuery(LocalDate.now().plusDays(2), LocalDate.now().plusDays(3));

        assertThatThrownBy(() -> generateUserTicketAverageReportUseCase.execute(query))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("Start date cannot be in the future");
    }

    @Test
    @DisplayName("Should throw exception when end date is in the future")
    void shouldThrowException_WhenEndDateIsInTheFuture() {
        UserTicketQuery query = new UserTicketQuery(
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(2));

        assertThatThrownBy(() -> generateUserTicketAverageReportUseCase.execute(query))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("End date cannot be in the future");
    }

    @Test
    @DisplayName("Should accept start and end date as today")
    void shouldAcceptStartAndEndDate_AsToday() {
        UserTicketQuery query = new UserTicketQuery(LocalDate.now(), LocalDate.now());
        List<UserTicketReport> expectedReport =
                Collections.singletonList(createUserTicketReport("user@example.com", new BigDecimal("50.00")));

        when(orderRepository.findUserTicketAverage(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedReport);

        List<UserTicketReport> result = generateUserTicketAverageReportUseCase.execute(query);

        assertThat(result).hasSize(1);

        verify(orderRepository).findUserTicketAverage(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should handle users with zero average ticket value")
    void shouldHandleUsers_WithZeroAverageTicketValue() {
        UserTicketQuery query = new UserTicketQuery(LocalDate.now().minusDays(7), LocalDate.now());
        List<UserTicketReport> expectedReport = Arrays.asList(
                createUserTicketReport("user1@example.com", BigDecimal.ZERO),
                createUserTicketReport("user2@example.com", new BigDecimal("100.00")));

        when(orderRepository.findUserTicketAverage(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedReport);

        List<UserTicketReport> result = generateUserTicketAverageReportUseCase.execute(query);

        assertThat(result)
                .hasSize(2)
                .first()
                .extracting(UserTicketReport::averageTicketValue)
                .isEqualTo(BigDecimal.ZERO);

        verify(orderRepository).findUserTicketAverage(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    private UserTicketReport createUserTicketReport(String email, BigDecimal averageValue) {
        return new UserTicketReport(UserId.generate(), email, averageValue);
    }
}
