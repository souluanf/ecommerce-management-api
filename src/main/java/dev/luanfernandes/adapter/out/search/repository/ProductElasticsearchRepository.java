package dev.luanfernandes.adapter.out.search.repository;

import dev.luanfernandes.adapter.out.search.document.ProductDocument;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductElasticsearchRepository extends ElasticsearchRepository<ProductDocument, String> {

    Page<ProductDocument> findByAvailableTrue(Pageable pageable);

    @Query(
            """
            {
              "bool": {
                "should": [
                  {
                    "multi_match": {
                      "query": "?0",
                      "fields": ["name^3", "description^1", "category^2"],
                      "type": "best_fields",
                      "fuzziness": "AUTO",
                      "prefix_length": 1
                    }
                  },
                  {
                    "multi_match": {
                      "query": "?0",
                      "fields": ["name^2", "description"],
                      "type": "phrase_prefix"
                    }
                  },
                  {
                    "wildcard": {
                      "name": {
                        "value": "*?0*",
                        "boost": 1.5
                      }
                    }
                  },
                  {
                    "wildcard": {
                      "description": "*?0*"
                    }
                  }
                ],
                "filter": [
                  {"term": {"available": true}}
                ],
                "minimum_should_match": 1
              }
            }
            """)
    Page<ProductDocument> findByQueryAndAvailableTrue(String query, Pageable pageable);

    @Query(
            "{\"bool\": {\"should\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description^1\", \"category^2\"], \"fuzziness\": \"AUTO\"}}, {\"wildcard\": {\"name\": \"*?0*\"}}, {\"wildcard\": {\"description\": \"*?0*\"}}, {\"prefix\": {\"name\": \"?0\"}}, {\"prefix\": {\"description\": \"?0\"}}], \"filter\": [{\"term\": {\"available\": true}}, {\"term\": {\"category\": \"?1\"}}], \"minimum_should_match\": 1}}")
    Page<ProductDocument> findByQueryAndCategoryAndAvailableTrue(String query, String category, Pageable pageable);

    @Query(
            "{\"bool\": {\"should\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description^1\", \"category^2\"], \"fuzziness\": \"AUTO\"}}, {\"wildcard\": {\"name\": \"*?0*\"}}, {\"wildcard\": {\"description\": \"*?0*\"}}, {\"prefix\": {\"name\": \"?0\"}}, {\"prefix\": {\"description\": \"?0\"}}], \"filter\": [{\"term\": {\"available\": true}}, {\"range\": {\"price\": {\"gte\": ?1, \"lte\": ?2}}}], \"minimum_should_match\": 1}}")
    Page<ProductDocument> findByQueryAndPriceRangeAndAvailableTrue(
            String query, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Query(
            "{\"bool\": {\"should\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description^1\", \"category^2\"], \"fuzziness\": \"AUTO\"}}, {\"wildcard\": {\"name\": \"*?0*\"}}, {\"wildcard\": {\"description\": \"*?0*\"}}, {\"prefix\": {\"name\": \"?0\"}}, {\"prefix\": {\"description\": \"?0\"}}], \"filter\": [{\"term\": {\"available\": true}}, {\"term\": {\"category\": \"?1\"}}, {\"range\": {\"price\": {\"gte\": ?2, \"lte\": ?3}}}], \"minimum_should_match\": 1}}")
    Page<ProductDocument> findByQueryAndCategoryAndPriceRangeAndAvailableTrue(
            String query, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<ProductDocument> findByCategoryAndAvailableTrue(String category, Pageable pageable);

    Page<ProductDocument> findByPriceBetweenAndAvailableTrue(
            BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<ProductDocument> findByCategoryAndPriceBetweenAndAvailableTrue(
            String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Query(
            "{\"suggest\": {\"name_suggest\": {\"prefix\": \"?0\", \"completion\": {\"field\": \"name.suggest\", \"size\": 10}}}}")
    List<String> findSuggestions(String prefix);
}
