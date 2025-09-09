package dev.luanfernandes.adapter.in.web.port.product;

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
public interface DeleteProductPort {

    @Operation(tags = "Products", summary = "Deletar produto", description = "Remove um produto do sistema")
    @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @ApiResponse(responseCode = "401", description = "Não autorizado")
    @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN")
    @ApiResponse(responseCode = "422", description = "Produto não pode ser deletado (tem pedidos associados)")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @DeleteMapping(PRODUCT_ID)
    ResponseEntity<Void> deleteProduct(@Parameter(description = "ID do produto") @PathVariable UUID id);
}
