package dev.luanfernandes.adapter.in.web.port.product;

import static dev.luanfernandes.infrastructure.constants.PathConstants.PRODUCTS_SEARCH_REINDEX;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dev.luanfernandes.domain.dto.ReindexResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(produces = APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "bearerAuth")
public interface ReindexProductsPort {

    @PostMapping(PRODUCTS_SEARCH_REINDEX)
    @Operation(
            tags = "Products",
            summary = "Reindex all products - Elasticsearch",
            description = "Reindex all products from database to Elasticsearch - Admin only")
    @ApiResponse(
            responseCode = "200",
            description = "Reindex completed successfully",
            content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ReindexResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Access denied - Admin only")
    @ApiResponse(responseCode = "500", description = "Reindex failed")
    ResponseEntity<ReindexResponse> reindexAllProducts();
}
