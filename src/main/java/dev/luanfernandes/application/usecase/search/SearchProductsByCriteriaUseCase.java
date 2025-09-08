package dev.luanfernandes.application.usecase.search;

import dev.luanfernandes.domain.dto.ProductSearchResult;
import dev.luanfernandes.domain.port.out.search.ProductSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SearchProductsByCriteriaUseCase {

    private static final Logger log = LoggerFactory.getLogger(SearchProductsByCriteriaUseCase.class);

    private final ProductSearchRepository productSearchRepository;

    public SearchProductsByCriteriaUseCase(ProductSearchRepository productSearchRepository) {
        this.productSearchRepository = productSearchRepository;
    }

    public ProductSearchResult execute(SearchProductsQuery query) {
        log.info(
                "SearchProductsByCriteria: Searching with criteria - name: '{}', category: '{}', minPrice: {}, maxPrice: {}",
                query.name(),
                query.category(),
                query.minPrice(),
                query.maxPrice());

        var searchCriteria = new ProductSearchRepository.SearchCriteria(
                query.name(), query.category(), query.minPrice(), query.maxPrice(), 0, 20);

        var result = productSearchRepository.search(searchCriteria);

        log.info("SearchProductsByCriteria: Found {} products matching criteria", result.size());

        return result;
    }

    public record SearchProductsQuery(String name, String category, Double minPrice, Double maxPrice) {}
}
