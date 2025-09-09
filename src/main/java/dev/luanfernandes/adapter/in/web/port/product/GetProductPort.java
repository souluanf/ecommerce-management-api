package dev.luanfernandes.adapter.in.web.port.product;

import static dev.luanfernandes.infrastructure.constants.PathConstants.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dev.luanfernandes.domain.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(produces = APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "bearerAuth")
public interface GetProductPort {

    @Operation(
            tags = "Products",
            summary = "Buscar produto por ID",
            description = "Retorna um produto específico pelo ID")
    @ApiResponse(
            responseCode = "200",
            description = "Produto encontrado",
            content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class)))
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping(PRODUCT_ID)
    ResponseEntity<ProductResponse> getProduct(@Parameter(description = "ID do produto") @PathVariable UUID id);
}
