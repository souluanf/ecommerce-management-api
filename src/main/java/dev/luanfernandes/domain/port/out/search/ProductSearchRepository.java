package dev.luanfernandes.domain.port.out.search;

import dev.luanfernandes.domain.dto.ProductSearchResult;
import dev.luanfernandes.domain.entity.ProductDomain;

public interface ProductSearchRepository {

    void index(ProductDomain product);

    void delete(ProductDomain product);

    ProductSearchResult search(SearchCriteria criteria);

    record SearchCriteria(String name, String category, Double minPrice, Double maxPrice, int page, int size) {}
}
