package dev.luanfernandes.adapter.in.web.port.report;

import static dev.luanfernandes.infrastructure.constants.PathConstants.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dev.luanfernandes.domain.dto.UserTicketReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(produces = APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "bearerAuth")
public interface GetUserTicketsReportPort {

    @Operation(
            tags = "Reports",
            summary = "Ticket médio por usuário",
            description = "Retorna o valor médio dos pedidos por usuário. Considera apenas pedidos pagos (PAID).")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Lista do ticket médio por usuário retornada com sucesso",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = UserTicketReportResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        """
                                [
                                  {
                                    "userId": "33333333-3333-3333-3333-333333333333",
                                    "email": "maria.santos@email.com",
                                    "averageTicket": 1067.99
                                  },
                                  {
                                    "userId": "22222222-2222-2222-2222-222222222222",
                                    "email": "joao.silva@email.com",
                                    "averageTicket": 599.99
                                  }
                                ]
                                """))),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping(REPORTS_USER_TICKETS)
    ResponseEntity<List<UserTicketReportResponse>> getUserTickets(
            @Parameter(description = "Data inicial para filtro (formato: YYYY-MM-DD)", example = "2025-01-01")
                    @RequestParam(required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate startDate,
            @Parameter(description = "Data final para filtro (formato: YYYY-MM-DD)", example = "2025-12-31")
                    @RequestParam(required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate endDate);
}
