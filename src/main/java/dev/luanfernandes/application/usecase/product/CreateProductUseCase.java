package dev.luanfernandes.application.usecase.product;

import dev.luanfernandes.domain.dto.command.CreateProductCommand;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.port.out.search.ProductSearchRepository;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    public ProductDomain create(CreateProductCommand command) {
        log.info(
                "CreateProduct: Creating new product - name: '{}', category: '{}', price: {}, stock: {}",
                command.name(),
                command.category(),
                command.price().value(),
                command.stockQuantity());

        ProductDomain product = new ProductDomain(
                ProductId.generate(),
                command.name(),
                command.description(),
                command.price(),
                command.category(),
                command.stockQuantity(),
                LocalDateTime.now());

        ProductDomain savedProduct = productRepository.save(product);
        log.info(
                "CreateProduct: Product saved to database - ID: {}",
                savedProduct.getId().value());

        try {
            productSearchRepository.index(savedProduct);
            log.info(
                    "CreateProduct: Product indexed for search - ID: {}",
                    savedProduct.getId().value());
        } catch (Exception e) {
            log.warn(
                    "CreateProduct: Failed to index product for search - ID: {}, error: {}",
                    savedProduct.getId().value(),
                    e.getMessage());
        }

        return savedProduct;
    }
}
