package dev.luanfernandes.adapter.in.web.port.product;

import static dev.luanfernandes.infrastructure.constants.PathConstants.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(produces = APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "bearerAuth")
public interface GetAllProductsPort {

    @Operation(
            tags = "Products",
            summary = "Listar todos os produtos",
            description = "Retorna lista paginada de produtos com filtros opcionais")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Lista paginada de produtos retornada com sucesso",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping(PRODUCTS)
    ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0")
                    int page_number,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int page_size,
            @Parameter(description = "Data de início para filtro") @RequestParam(required = false) LocalDate start_date,
            @Parameter(description = "Data de fim para filtro") @RequestParam(required = false) LocalDate end_date,
            @Parameter(description = "Ordenação (ex: name,asc ou price,desc)") @RequestParam(required = false)
                    String sort);
}
