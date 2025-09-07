package dev.luanfernandes.adapter.in.web.adapter.product;

import dev.luanfernandes.adapter.in.web.port.product.GetAllProductsPort;
import dev.luanfernandes.application.usecase.product.ListAllProductsUseCase;
import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.dto.ProductResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GetAllProductsAdapter implements GetAllProductsPort {

    private final ListAllProductsUseCase listAllProductsUseCase;

    @Override
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            int pageNumber, int pageSize, LocalDate startDate, LocalDate endDate, String sort) {

        log.debug("Fetching products with pagination - page: {}, size: {}", pageNumber, pageSize);

        var pageRequest = PageRequest.of(pageNumber, pageSize, startDate, endDate, sort);
        var pagedProducts = listAllProductsUseCase.execute(pageRequest);

        var responseContent =
                pagedProducts.content().stream().map(ProductResponse::from).toList();

        var response = PageResponse.of(
                pagedProducts.pageNumber(), pagedProducts.pageSize(), pagedProducts.elements(), responseContent);

        return ResponseEntity.ok(response);
    }
}
