package dev.luanfernandes.application.usecase.product;

import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ListAllProductsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListAllProductsUseCase.class);

    private final ProductRepository productRepository;

    public ListAllProductsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDomain> execute() {
        log.info("ListAllProducts: Searching for all products");
        var result = productRepository.findAll();
        log.info("ListAllProducts: Found {} products", result.size());
        return result;
    }

    public PageResponse<ProductDomain> execute(PageRequest pageRequest) {
        log.info(
                "ListAllProducts: Searching for products with pagination - page: {}, size: {}",
                pageRequest.pageNumber(),
                pageRequest.pageSize());

        var result = productRepository.findAllPaginated(pageRequest);

        log.info(
                "ListAllProducts: Found {} products on page {} of {}",
                result.content().size(),
                pageRequest.pageNumber(),
                result.getTotalPages());

        return result;
    }
}
