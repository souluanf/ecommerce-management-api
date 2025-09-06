package dev.luanfernandes.application.usecase.product;

import dev.luanfernandes.domain.dto.command.UpdateProductCommand;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.port.out.search.ProductSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateProductUseCase.class);

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    public UpdateProductUseCase(ProductRepository productRepository, ProductSearchRepository productSearchRepository) {
        this.productRepository = productRepository;
        this.productSearchRepository = productSearchRepository;
    }

    public Optional<ProductDomain> update(UpdateProductCommand command) {
        log.info(
                "UpdateProduct: Updating product ID: {} - name: '{}', category: '{}', price: {}, stock: {}",
                command.id().value(),
                command.name(),
                command.category(),
                command.price().value(),
                command.stockQuantity());

        return productRepository
                .findById(command.id())
                .map(existingProduct -> {
                    var updatedProduct = new ProductDomain(
                            existingProduct.getId(),
                            command.name(),
                            command.description(),
                            command.price(),
                            command.category(),
                            command.stockQuantity(),
                            existingProduct.getCreatedAt());

                    var savedProduct = productRepository.save(updatedProduct);
                    log.info(
                            "UpdateProduct: Product updated successfully - ID: {}",
                            savedProduct.getId().value());

                    try {
                        productSearchRepository.index(savedProduct);
                        log.info(
                                "UpdateProduct: Product reindexed for search - ID: {}",
                                savedProduct.getId().value());
                    } catch (Exception e) {
                        log.warn(
                                "UpdateProduct: Failed to reindex product for search - ID: {}, error: {}",
                                savedProduct.getId().value(),
                                e.getMessage());
                    }

                    return savedProduct;
                })
                .or(() -> {
                    log.warn(
                            "UpdateProduct: Product not found for update - ID: {}",
                            command.id().value());
                    return Optional.empty();
                });
    }
}
