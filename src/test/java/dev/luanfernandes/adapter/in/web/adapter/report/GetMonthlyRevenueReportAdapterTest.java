package dev.luanfernandes.adapter.in.web.adapter.report;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.luanfernandes.application.usecase.report.GetCurrentMonthRevenueUseCase;
import dev.luanfernandes.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(
        classes = {
            GetMonthlyRevenueReportAdapter.class,
            dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class
        })
class GetMonthlyRevenueReportAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetCurrentMonthRevenueUseCase getCurrentMonthRevenueUseCase;

    @Test
    void shouldGetMonthlyRevenueSuccessfully() throws Exception {
        Money revenue = Money.of(new BigDecimal("15000.50"));
        LocalDateTime now = LocalDateTime.now();

        when(getCurrentMonthRevenueUseCase.execute()).thenReturn(revenue);

        mockMvc.perform(get("/api/v1/reports/monthly-revenue").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.year").value(now.getYear()))
                .andExpect(jsonPath("$.month").value(now.getMonthValue()))
                .andExpect(jsonPath("$.totalRevenue").value(15000.50));
    }

    @Test
    void shouldGetMonthlyRevenueWithZeroRevenue() throws Exception {
        Money revenue = Money.of(BigDecimal.ZERO);
        LocalDateTime now = LocalDateTime.now();

        when(getCurrentMonthRevenueUseCase.execute()).thenReturn(revenue);

        mockMvc.perform(get("/api/v1/reports/monthly-revenue").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(now.getYear()))
                .andExpect(jsonPath("$.month").value(now.getMonthValue()))
                .andExpect(jsonPath("$.totalRevenue").value(0.00));
    }

    @Test
    void shouldGetMonthlyRevenueWithHighValue() throws Exception {
        Money revenue = Money.of(new BigDecimal("999999.99"));

        when(getCurrentMonthRevenueUseCase.execute()).thenReturn(revenue);

        mockMvc.perform(get("/api/v1/reports/monthly-revenue").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(999999.99));
    }
}
