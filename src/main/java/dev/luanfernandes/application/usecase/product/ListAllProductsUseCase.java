package dev.luanfernandes.application.usecase.product;

import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListAllProductsUseCase {

    private final ProductRepository productRepository;

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
