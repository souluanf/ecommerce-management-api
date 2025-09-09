package dev.luanfernandes.adapter.in.web.port.report;

import static dev.luanfernandes.infrastructure.constants.PathConstants.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dev.luanfernandes.domain.dto.MonthlyRevenueResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(produces = APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "bearerAuth")
public interface GetMonthlyRevenueReportPort {

    @Operation(
            tags = "Reports",
            summary = "Faturamento do mês atual",
            description = "Retorna o faturamento total do mês corrente. Considera apenas pedidos pagos (PAID).")
    @ApiResponse(
            responseCode = "200",
            description = "Faturamento mensal retornado com sucesso",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MonthlyRevenueResponse.class),
                            examples =
                                    @ExampleObject(
                                            value =
                                                    """
                                {
                                  "year": 2025,
                                  "month": 9,
                                  "totalRevenue": 2800.00
                                }
                                """)))
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping(REPORTS_MONTHLY_REVENUE)
    ResponseEntity<MonthlyRevenueResponse> getMonthlyRevenue();
}
