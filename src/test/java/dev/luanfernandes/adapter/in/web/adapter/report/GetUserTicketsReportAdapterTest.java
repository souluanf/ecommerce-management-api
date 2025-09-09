package dev.luanfernandes.adapter.in.web.adapter.report;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.luanfernandes.application.usecase.report.GenerateUserTicketAverageReportUseCase;
import dev.luanfernandes.domain.dto.query.UserTicketQuery;
import dev.luanfernandes.domain.dto.result.UserTicketReport;
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
            GetUserTicketsReportAdapter.class,
            dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class
        })
class GetUserTicketsReportAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenerateUserTicketAverageReportUseCase generateUserTicketAverageReportUseCase;

    @Test
    void shouldGetUserTicketsReportSuccessfully() throws Exception {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID userId3 = UUID.randomUUID();

        List<UserTicketReport> userTickets = List.of(
                new UserTicketReport(UserId.of(userId1), "Ana Costa", new BigDecimal("250.75")),
                new UserTicketReport(UserId.of(userId2), "Carlos Lima", new BigDecimal("180.50")),
                new UserTicketReport(UserId.of(userId3), "Paula Souza", new BigDecimal("320.00")));

        when(generateUserTicketAverageReportUseCase.execute(any(UserTicketQuery.class)))
                .thenReturn(userTickets);

        mockMvc.perform(get("/api/v1/reports/user-tickets")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-12-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].userId").value(userId1.toString()))
                .andExpect(jsonPath("$[0].email").value("Ana Costa"))
                .andExpect(jsonPath("$[0].averageTicket").value(250.75))
                .andExpect(jsonPath("$[1].userId").value(userId2.toString()))
                .andExpect(jsonPath("$[1].email").value("Carlos Lima"))
                .andExpect(jsonPath("$[1].averageTicket").value(180.50))
                .andExpect(jsonPath("$[2].userId").value(userId3.toString()))
                .andExpect(jsonPath("$[2].email").value("Paula Souza"))
                .andExpect(jsonPath("$[2].averageTicket").value(320.00));
    }

    @Test
    void shouldReturnEmptyListWhenNoUserTickets() throws Exception {
        when(generateUserTicketAverageReportUseCase.execute(any(UserTicketQuery.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/reports/user-tickets")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldGetUserTicketsWithCustomDateRange() throws Exception {
        UUID userId = UUID.randomUUID();

        List<UserTicketReport> userTickets =
                List.of(new UserTicketReport(UserId.of(userId), "Cliente VIP", new BigDecimal("1500.00")));

        when(generateUserTicketAverageReportUseCase.execute(any(UserTicketQuery.class)))
                .thenReturn(userTickets);

        mockMvc.perform(get("/api/v1/reports/user-tickets")
                        .param("startDate", "2024-06-01")
                        .param("endDate", "2024-06-30")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].averageTicket").value(1500.00));
    }

    @Test
    void shouldGetUserTicketsWithCurrentMonth() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);

        when(generateUserTicketAverageReportUseCase.execute(any(UserTicketQuery.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/reports/user-tickets")
                        .param("startDate", firstDayOfMonth.toString())
                        .param("endDate", today.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetUserTicketsWithZeroAverage() throws Exception {
        UUID userId = UUID.randomUUID();

        List<UserTicketReport> userTickets =
                List.of(new UserTicketReport(UserId.of(userId), "Novo Cliente", BigDecimal.ZERO));

        when(generateUserTicketAverageReportUseCase.execute(any(UserTicketQuery.class)))
                .thenReturn(userTickets);

        mockMvc.perform(get("/api/v1/reports/user-tickets")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].averageTicket").value(0.00));
    }
}
