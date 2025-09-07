package dev.luanfernandes.adapter.in.web.port.product;

import static dev.luanfernandes.infrastructure.constants.PathConstants.PRODUCTS_SEARCH;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dev.luanfernandes.adapter.out.search.domain.SearchResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(produces = APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "bearerAuth")
public interface SearchProductsPort {

    @GetMapping(PRODUCTS_SEARCH)
    @Operation(
            tags = "Products",
            summary = "Search products - Elasticsearch",
            description = "Search for products using Elasticsearch with fuzzy matching")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Search results returned successfully",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = SearchResult.class))),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<SearchResult> searchProducts(
            @Parameter(description = "Search query") @RequestParam(required = false) String q,
            @Parameter(description = "Product category") @RequestParam(required = false) String category,
            @Parameter(description = "Minimum price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(required = false) String sortField,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "ASC")
                    String sortDirection);
}
