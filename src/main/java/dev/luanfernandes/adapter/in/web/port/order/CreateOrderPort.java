package dev.luanfernandes.adapter.in.web.port.order;

import static dev.luanfernandes.infrastructure.constants.PathConstants.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dev.luanfernandes.domain.dto.CreateOrderRequest;
import dev.luanfernandes.domain.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(produces = APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "bearerAuth")
public interface CreateOrderPort {

    @Operation(
            tags = "Orders",
            summary = "Criar pedido",
            description = "Cria um novo pedido com os produtos especificados")
    @ApiResponse(
            responseCode = "201",
            description = "Pedido criado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou produtos não disponíveis")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PostMapping(ORDERS)
    ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request);
}
