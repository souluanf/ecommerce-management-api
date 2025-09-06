package dev.luanfernandes.application.usecase.product;

import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.ProductNotFoundException;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.valueobject.ProductId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FindProductByIdUseCase {

    private static final Logger log = LoggerFactory.getLogger(FindProductByIdUseCase.class);

    private final ProductRepository productRepository;

    public FindProductByIdUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDomain execute(ProductId id) {
        log.info("FindProductById: Searching for product with ID: {}", id.value());

        var result = productRepository.findById(id);

        if (result.isPresent()) {
            log.info(
                    "FindProductById: Product found - ID: {}, Name: '{}'",
                    id.value(),
                    result.get().getName());
            return result.get();
        } else {
            log.warn("FindProductById: Product NOT found with ID: {}", id.value());
            throw new ProductNotFoundException(id.value());
        }
    }
}
