package dev.luanfernandes.adapter.out.search.repository;

import dev.luanfernandes.adapter.out.search.document.ProductDocument;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.port.out.search.ProductSearchRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchRepositoryImpl implements ProductSearchRepository {

    private final ProductElasticsearchRepository elasticsearchRepository;

    @Override
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000))
    public void index(ProductDomain product) {
        try {
            ProductDocument document = ProductDocument.from(
                    product.getId().value(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice().value(),
                    product.getCategory(),
                    product.getStockQuantity(),
                    product.getCreatedAt(),
                    product.getUpdatedAt());

            elasticsearchRepository.save(document);

            log.info(
                    "ES: Product indexed successfully - id: '{}', name: '{}', available: {}",
                    product.getId().value(),
                    product.getName(),
                    document.isInStock());
        } catch (Exception e) {
            log.error(
                    "ES: Failed to index product - id: '{}', error: {}",
                    product.getId().value(),
                    e.getMessage(),
                    e);
            throw e;
        }
    }

    @Override
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000))
    public void delete(ProductDomain product) {
        try {
            elasticsearchRepository.deleteById(product.getId().value());
            log.info(
                    "ES: Product deleted from index - id: '{}'", product.getId().value());
        } catch (Exception e) {
            log.error(
                    "ES: Failed to delete product from index - id: '{}', error: {}",
                    product.getId().value(),
                    e.getMessage(),
                    e);
            throw e;
        }
    }

    @Override
    public dev.luanfernandes.domain.dto.ProductSearchResult search(SearchCriteria criteria) {
        try {
            long startTime = System.currentTimeMillis();

            org.springframework.data.domain.Page<ProductDocument> page = performDirectSearch(criteria);

            List<ProductDomain> results =
                    page.getContent().stream().map(this::convertToDomain).toList();

            long executionTime = System.currentTimeMillis() - startTime;
            log.info(
                    "🔍 ES: Domain search completed - query: '{}', category: '{}', priceRange: {}-{}, results: {}, time: {}ms",
                    criteria.name(),
                    criteria.category(),
                    criteria.minPrice(),
                    criteria.maxPrice(),
                    results.size(),
                    executionTime);

            if (executionTime > 300) {
                log.warn("ES: Slow domain search detected - query: '{}', time: {}ms", criteria.name(), executionTime);
            }

            return dev.luanfernandes.domain.dto.ProductSearchResult.of(
                    results, page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize());
        } catch (Exception e) {
            log.error("ES: Domain search failed - query: '{}', error: {}", criteria.name(), e.getMessage(), e);
            return dev.luanfernandes.domain.dto.ProductSearchResult.empty();
        }
    }

    private org.springframework.data.domain.Page<ProductDocument> performDirectSearch(SearchCriteria criteria) {
        org.springframework.data.domain.PageRequest pageRequest =
                org.springframework.data.domain.PageRequest.of(criteria.page(), criteria.size());

        boolean hasQuery = criteria.name() != null && !criteria.name().trim().isEmpty();
        boolean hasCategory =
                criteria.category() != null && !criteria.category().trim().isEmpty();
        boolean hasPriceRange = criteria.minPrice() != null || criteria.maxPrice() != null;

        if (!hasQuery) {
            return searchWithoutQuery(criteria, hasCategory, hasPriceRange, pageRequest);
        }

        return searchWithQuery(criteria, hasCategory, hasPriceRange, pageRequest);
    }

    private org.springframework.data.domain.Page<ProductDocument> searchWithoutQuery(
            SearchCriteria criteria,
            boolean hasCategory,
            boolean hasPriceRange,
            org.springframework.data.domain.PageRequest pageRequest) {

        if (hasCategory && hasPriceRange) {
            BigDecimal minPrice =
                    criteria.minPrice() != null ? BigDecimal.valueOf(criteria.minPrice()) : BigDecimal.ZERO;
            BigDecimal maxPrice = criteria.maxPrice() != null
                    ? BigDecimal.valueOf(criteria.maxPrice())
                    : BigDecimal.valueOf(Double.MAX_VALUE);
            return elasticsearchRepository.findByCategoryAndPriceBetweenAndAvailableTrue(
                    criteria.category(), minPrice, maxPrice, pageRequest);
        }

        if (hasCategory) {
            return elasticsearchRepository.findByCategoryAndAvailableTrue(criteria.category(), pageRequest);
        }

        if (hasPriceRange) {
            BigDecimal minPrice =
                    criteria.minPrice() != null ? BigDecimal.valueOf(criteria.minPrice()) : BigDecimal.ZERO;
            BigDecimal maxPrice = criteria.maxPrice() != null
                    ? BigDecimal.valueOf(criteria.maxPrice())
                    : BigDecimal.valueOf(Double.MAX_VALUE);
            return elasticsearchRepository.findByPriceBetweenAndAvailableTrue(minPrice, maxPrice, pageRequest);
        }

        return elasticsearchRepository.findByAvailableTrue(pageRequest);
    }

    private org.springframework.data.domain.Page<ProductDocument> searchWithQuery(
            SearchCriteria criteria,
            boolean hasCategory,
            boolean hasPriceRange,
            org.springframework.data.domain.PageRequest pageRequest) {

        if (hasCategory && hasPriceRange) {
            BigDecimal minPrice =
                    criteria.minPrice() != null ? BigDecimal.valueOf(criteria.minPrice()) : BigDecimal.ZERO;
            BigDecimal maxPrice = criteria.maxPrice() != null
                    ? BigDecimal.valueOf(criteria.maxPrice())
                    : BigDecimal.valueOf(Double.MAX_VALUE);
            return elasticsearchRepository.findByQueryAndCategoryAndPriceRangeAndAvailableTrue(
                    criteria.name(), criteria.category(), minPrice, maxPrice, pageRequest);
        }

        if (hasCategory) {
            return elasticsearchRepository.findByQueryAndCategoryAndAvailableTrue(
                    criteria.name(), criteria.category(), pageRequest);
        }

        if (hasPriceRange) {
            BigDecimal minPrice =
                    criteria.minPrice() != null ? BigDecimal.valueOf(criteria.minPrice()) : BigDecimal.ZERO;
            BigDecimal maxPrice = criteria.maxPrice() != null
                    ? BigDecimal.valueOf(criteria.maxPrice())
                    : BigDecimal.valueOf(Double.MAX_VALUE);
            return elasticsearchRepository.findByQueryAndPriceRangeAndAvailableTrue(
                    criteria.name(), minPrice, maxPrice, pageRequest);
        }

        return elasticsearchRepository.findByQueryAndAvailableTrue(criteria.name(), pageRequest);
    }

    private ProductDomain convertToDomain(ProductDocument document) {
        return new ProductDomain(
                new dev.luanfernandes.domain.valueobject.ProductId(document.id()),
                document.name(),
                document.description(),
                new dev.luanfernandes.domain.valueobject.Money(document.price()),
                document.category(),
                document.stockQuantity(),
                document.createdAt());
    }
}
