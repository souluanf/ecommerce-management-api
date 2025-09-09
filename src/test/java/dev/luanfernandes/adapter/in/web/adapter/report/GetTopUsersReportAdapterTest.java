package dev.luanfernandes.adapter.in.web.adapter.report;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.luanfernandes.application.usecase.report.GenerateTopUsersReportUseCase;
import dev.luanfernandes.domain.dto.query.TopUsersQuery;
import dev.luanfernandes.domain.dto.result.TopUserReport;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.UserId;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
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
            GetTopUsersReportAdapter.class,
            dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class
        })
class GetTopUsersReportAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenerateTopUsersReportUseCase generateTopUsersReportUseCase;

    @Test
    void shouldGetTopUsersReportSuccessfully() throws Exception {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        List<TopUserReport> topUsers = List.of(
                new TopUserReport(UserId.of(userId1), "João Silva", Money.of(new BigDecimal("2500.00")), 15),
                new TopUserReport(UserId.of(userId2), "Maria Santos", Money.of(new BigDecimal("1800.50")), 12));

        when(generateTopUsersReportUseCase.execute(any(TopUsersQuery.class))).thenReturn(topUsers);

        mockMvc.perform(get("/api/v1/reports/top-users")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-12-31")
                        .param("limit", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(userId1.toString()))
                .andExpect(jsonPath("$[0].email").value("João Silva"))
                .andExpect(jsonPath("$[0].totalSpent").value(2500.00))
                .andExpect(jsonPath("$[0].orderCount").value(15))
                .andExpect(jsonPath("$[1].userId").value(userId2.toString()))
                .andExpect(jsonPath("$[1].email").value("Maria Santos"))
                .andExpect(jsonPath("$[1].totalSpent").value(1800.50))
                .andExpect(jsonPath("$[1].orderCount").value(12));
    }

    @Test
    void shouldReturnEmptyListWhenNoTopUsers() throws Exception {
        when(generateTopUsersReportUseCase.execute(any(TopUsersQuery.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/reports/top-users")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31")
                        .param("limit", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldGetTopUsersWithCustomLimit() throws Exception {
        UUID userId = UUID.randomUUID();

        List<TopUserReport> topUsers =
                List.of(new TopUserReport(UserId.of(userId), "Top User", Money.of(new BigDecimal("5000.00")), 25));

        when(generateTopUsersReportUseCase.execute(any(TopUsersQuery.class))).thenReturn(topUsers);

        mockMvc.perform(get("/api/v1/reports/top-users")
                        .param("startDate", "2024-06-01")
                        .param("endDate", "2024-06-30")
                        .param("limit", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].totalSpent").value(5000.00));
    }

    @Test
    void shouldGetTopUsersWithCurrentDateRange() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate lastMonth = today.minusMonths(1);

        when(generateTopUsersReportUseCase.execute(any(TopUsersQuery.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/reports/top-users")
                        .param("startDate", lastMonth.toString())
                        .param("endDate", today.toString())
                        .param("limit", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
