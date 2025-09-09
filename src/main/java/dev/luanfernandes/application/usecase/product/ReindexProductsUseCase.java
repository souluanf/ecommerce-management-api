package dev.luanfernandes.application.usecase.product;

import dev.luanfernandes.domain.dto.result.ReindexResult;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.ReindexFailedException;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.port.out.search.ProductSearchRepository;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReindexProductsUseCase {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    public ReindexResult execute() {
        log.info("ReindexProducts: Starting full product reindex");

        try {
            List<ProductDomain> allProducts = productRepository.findAll();

            if (allProducts.isEmpty()) {
                log.warn("ReindexProducts: No products found to reindex");
                return new ReindexResult(0, 0, "No products found");
            }

            log.info("ReindexProducts: Found {} products to reindex", allProducts.size());

            AtomicInteger indexedCount = new AtomicInteger(0);
            AtomicInteger failedCount = new AtomicInteger(0);

            allProducts.forEach(product -> {
                try {
                    productSearchRepository.index(product);
                    indexedCount.incrementAndGet();
                } catch (Exception e) {
                    failedCount.incrementAndGet();
                    log.error(
                            "ReindexProducts: Failed to index product {} - error: {}",
                            product.getId().value(),
                            e.getMessage());
                }
            });

            int totalProducts = allProducts.size();
            int indexed = indexedCount.get();
            int failed = failedCount.get();

            if (failed > 0) {
                log.warn(
                        "ReindexProducts: Completed with issues - Total: {}, Indexed: {}, Failed: {}",
                        totalProducts,
                        indexed,
                        failed);
                return new ReindexResult(
                        totalProducts, indexed, String.format("Reindex completed with %d failures", failed));
            }

            log.info("ReindexProducts: Successfully reindexed all {} products", indexed);
            return new ReindexResult(totalProducts, indexed, "Reindex completed successfully");

        } catch (Exception e) {
            log.error("ReindexProducts: Fatal error during reindex - error: {}", e.getMessage(), e);
            throw new ReindexFailedException("Reindex failed: " + e.getMessage(), e);
        }
    }
}
