package dev.luanfernandes.application.usecase.search;

import dev.luanfernandes.adapter.out.search.domain.SearchCriteria;
import dev.luanfernandes.domain.dto.ProductSearchResult;
import dev.luanfernandes.domain.port.out.search.ProductSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SearchProductsUseCase {

    private static final Logger log = LoggerFactory.getLogger(SearchProductsUseCase.class);

    private final ProductSearchRepository productSearchRepository;

    public SearchProductsUseCase(ProductSearchRepository productSearchRepository) {
        this.productSearchRepository = productSearchRepository;
    }

    public ProductSearchResult execute(SearchCriteria criteria) {
        log.info(
                "SearchProducts: Executing search - query: '{}', category: '{}', priceRange: {}-{}, page: {}, size: {}",
                criteria.query(),
                criteria.category(),
                criteria.minPrice(),
                criteria.maxPrice(),
                criteria.page(),
                criteria.size());

        ProductSearchRepository.SearchCriteria domainCriteria = new ProductSearchRepository.SearchCriteria(
                criteria.query(),
                criteria.category(),
                criteria.minPrice() != null ? criteria.minPrice().doubleValue() : null,
                criteria.maxPrice() != null ? criteria.maxPrice().doubleValue() : null,
                criteria.page(),
                criteria.size());

        ProductSearchResult result = productSearchRepository.search(domainCriteria);

        log.info("SearchProducts: Found {} products", result.size());

        return result;
    }
}
