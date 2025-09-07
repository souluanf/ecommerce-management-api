package dev.luanfernandes.adapter.in.web.adapter.product;

import dev.luanfernandes.adapter.in.web.port.product.SearchProductsPort;
import dev.luanfernandes.adapter.out.search.document.ProductDocument;
import dev.luanfernandes.adapter.out.search.domain.SearchCriteria;
import dev.luanfernandes.adapter.out.search.domain.SearchResult;
import dev.luanfernandes.application.usecase.search.SearchProductsUseCase;
import dev.luanfernandes.domain.dto.ProductSearchResult;
import dev.luanfernandes.domain.entity.ProductDomain;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchProductsAdapter implements SearchProductsPort {

    private final SearchProductsUseCase searchProductsUseCase;

    @Override
    public ResponseEntity<SearchResult> searchProducts(
            String q,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortField,
            String sortDirection) {

        log.info(
                "Product search request - q: '{}', category: '{}', priceRange: {}-{}, page: {}, size: {}",
                q,
                category,
                minPrice,
                maxPrice,
                page,
                size);

        SearchCriteria criteria =
                new SearchCriteria(q, category, minPrice, maxPrice, page, size, sortField, sortDirection);
        ProductSearchResult domainResult = searchProductsUseCase.execute(criteria);

        SearchResult result = convertToAdapterResult(domainResult);

        return ResponseEntity.ok(result);
    }

    private SearchResult convertToAdapterResult(ProductSearchResult domainResult) {
        List<ProductDocument> documents =
                domainResult.products().stream().map(this::convertToDocument).toList();

        return SearchResult.of(
                documents,
                domainResult.totalElements(),
                domainResult.totalPages(),
                domainResult.currentPage(),
                domainResult.pageSize());
    }

    private ProductDocument convertToDocument(ProductDomain product) {
        return ProductDocument.from(
                product.getId().value(),
                product.getName(),
                product.getDescription(),
                product.getPrice().value(),
                product.getCategory(),
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }
}
