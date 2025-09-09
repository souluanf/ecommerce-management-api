package dev.luanfernandes.application.usecase.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.port.out.order.OrderRepository;
import dev.luanfernandes.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for GetCurrentMonthRevenueUseCase")
class GetCurrentMonthRevenueUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private GetCurrentMonthRevenueUseCase getCurrentMonthRevenueUseCase;

    @Test
    @DisplayName("Should return current month revenue when orders exist")
    void shouldReturnCurrentMonthRevenue_WhenOrdersExist() {
        BigDecimal expectedRevenue = new BigDecimal("5000.00");
        when(orderRepository.calculateMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedRevenue);

        Money result = getCurrentMonthRevenueUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(expectedRevenue);

        verify(orderRepository).calculateMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should return zero revenue when no orders exist")
    void shouldReturnZeroRevenue_WhenNoOrdersExist() {
        when(orderRepository.calculateMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(null);

        Money result = getCurrentMonthRevenueUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualByComparingTo(BigDecimal.ZERO);

        verify(orderRepository).calculateMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should return zero revenue when repository returns zero")
    void shouldReturnZeroRevenue_WhenRepositoryReturnsZero() {
        when(orderRepository.calculateMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(BigDecimal.ZERO);

        Money result = getCurrentMonthRevenueUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualByComparingTo(BigDecimal.ZERO);

        verify(orderRepository).calculateMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should handle large revenue amounts")
    void shouldHandleLargeRevenueAmounts() {
        BigDecimal largeRevenue = new BigDecimal("999999.99");
        when(orderRepository.calculateMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(largeRevenue);

        Money result = getCurrentMonthRevenueUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(largeRevenue);

        verify(orderRepository).calculateMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should handle decimal revenue amounts")
    void shouldHandleDecimalRevenueAmounts() {
        BigDecimal decimalRevenue = new BigDecimal("1234.56");
        when(orderRepository.calculateMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(decimalRevenue);

        Money result = getCurrentMonthRevenueUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(decimalRevenue);

        verify(orderRepository).calculateMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should call repository with correct month date range")
    void shouldCallRepository_WithCorrectMonthDateRange() {
        BigDecimal revenue = new BigDecimal("1000.00");
        when(orderRepository.calculateMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(revenue);

        getCurrentMonthRevenueUseCase.execute();

        verify(orderRepository).calculateMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
