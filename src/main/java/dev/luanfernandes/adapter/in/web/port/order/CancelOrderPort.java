package dev.luanfernandes.adapter.in.web.port.order;

import static dev.luanfernandes.infrastructure.constants.PathConstants.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(produces = APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "bearerAuth")
public interface CancelOrderPort {

    @Operation(tags = "Orders", summary = "Cancelar pedido", description = "Cancela um pedido pendente")
    @ApiResponse(responseCode = "204", description = "Pedido cancelado com sucesso")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "422", description = "Pedido não pode ser cancelado (já pago ou cancelado)")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @DeleteMapping(ORDER_ID)
    ResponseEntity<Void> cancelOrder(@Parameter(description = "ID do pedido") @PathVariable UUID id);
}
