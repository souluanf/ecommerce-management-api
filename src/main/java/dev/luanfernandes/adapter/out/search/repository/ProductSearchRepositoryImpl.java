package dev.luanfernandes.adapter.out.search.repository;

import static java.lang.Double.MAX_VALUE;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;

import dev.luanfernandes.adapter.out.search.document.ProductDocument;
import dev.luanfernandes.domain.dto.ProductSearchResult;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.SearchException;
import dev.luanfernandes.domain.port.out.search.ProductSearchRepository;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
            retryFor = {Exception.class},
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
            throw new SearchException("index", product.getId().value(), e.getMessage(), e);
        }
    }

    @Override
    @Retryable(
            retryFor = {Exception.class},
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
            throw new SearchException("delete", product.getId().value(), e.getMessage(), e);
        }
    }

    @Override
    public ProductSearchResult search(SearchCriteria criteria) {
        try {
            long startTime = System.currentTimeMillis();

            Page<ProductDocument> page = performDirectSearch(criteria);

            List<ProductDomain> results =
                    page.getContent().stream().map(this::convertToDomain).toList();

            long executionTime = System.currentTimeMillis() - startTime;
            log.info(
                    "ES: Domain search completed - query: '{}', category: '{}', priceRange: {}-{}, results: {}, time: {}ms",
                    criteria.name(),
                    criteria.category(),
                    criteria.minPrice(),
                    criteria.maxPrice(),
                    results.size(),
                    executionTime);

            return ProductSearchResult.of(
                    results, page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize());
        } catch (Exception e) {
            log.error("ES: Domain search failed - query: '{}', error: {}", criteria.name(), e.getMessage(), e);
            return ProductSearchResult.empty();
        }
    }

    private Page<ProductDocument> performDirectSearch(SearchCriteria criteria) {
        PageRequest pageRequest = PageRequest.of(criteria.page(), criteria.size());

        boolean hasQuery = criteria.name() != null && !criteria.name().trim().isEmpty();
        boolean hasCategory =
                criteria.category() != null && !criteria.category().trim().isEmpty();
        boolean hasPriceRange = criteria.minPrice() != null || criteria.maxPrice() != null;

        if (!hasQuery) {
            return searchWithoutQuery(criteria, hasCategory, hasPriceRange, pageRequest);
        }

        return searchWithQuery(criteria, hasCategory, hasPriceRange, pageRequest);
    }

    private Page<ProductDocument> searchWithoutQuery(
            SearchCriteria criteria, boolean hasCategory, boolean hasPriceRange, PageRequest pageRequest) {

        if (hasCategory && hasPriceRange) {
            return elasticsearchRepository.findByCategoryAndPriceBetweenAndAvailableTrue(
                    criteria.category(), getMinPrice(criteria), getMaxPrice(criteria), pageRequest);
        }

        if (hasCategory) {
            return elasticsearchRepository.findByCategoryAndAvailableTrue(criteria.category(), pageRequest);
        }

        if (hasPriceRange) {
            return elasticsearchRepository.findByPriceBetweenAndAvailableTrue(
                    getMinPrice(criteria), getMaxPrice(criteria), pageRequest);
        }

        return elasticsearchRepository.findByAvailableTrue(pageRequest);
    }

    private Page<ProductDocument> searchWithQuery(
            SearchCriteria criteria, boolean hasCategory, boolean hasPriceRange, PageRequest pageRequest) {

        if (hasCategory && hasPriceRange) {
            return elasticsearchRepository.findByQueryAndCategoryAndPriceRangeAndAvailableTrue(
                    criteria.name(), criteria.category(), getMinPrice(criteria), getMaxPrice(criteria), pageRequest);
        }

        if (hasCategory) {
            return elasticsearchRepository.findByQueryAndCategoryAndAvailableTrue(
                    criteria.name(), criteria.category(), pageRequest);
        }

        if (hasPriceRange) {
            return elasticsearchRepository.findByQueryAndPriceRangeAndAvailableTrue(
                    criteria.name(), getMinPrice(criteria), getMaxPrice(criteria), pageRequest);
        }

        return elasticsearchRepository.findByQueryAndAvailableTrue(criteria.name(), pageRequest);
    }

    private ProductDomain convertToDomain(ProductDocument document) {
        return new ProductDomain(
                new ProductId(document.id()),
                document.name(),
                document.description(),
                new Money(document.price()),
                document.category(),
                document.stockQuantity(),
                document.createdAt());
    }

    private BigDecimal getMinPrice(SearchCriteria criteria) {
        return criteria.minPrice() != null ? valueOf(criteria.minPrice()) : ZERO;
    }

    private BigDecimal getMaxPrice(SearchCriteria criteria) {
        return criteria.maxPrice() != null ? valueOf(criteria.maxPrice()) : valueOf(MAX_VALUE);
    }
}
