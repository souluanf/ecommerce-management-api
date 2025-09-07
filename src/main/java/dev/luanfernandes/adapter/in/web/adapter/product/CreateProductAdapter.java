package dev.luanfernandes.adapter.in.web.adapter.product;

import dev.luanfernandes.adapter.in.web.port.product.CreateProductPort;
import dev.luanfernandes.application.usecase.product.CreateProductUseCase;
import dev.luanfernandes.domain.dto.CreateProductRequest;
import dev.luanfernandes.domain.dto.ProductResponse;
import dev.luanfernandes.domain.dto.command.CreateProductCommand;
import dev.luanfernandes.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CreateProductAdapter implements CreateProductPort {

    private final CreateProductUseCase createProductUseCase;

    @Override
    public ResponseEntity<ProductResponse> createProduct(CreateProductRequest request) {
        log.info("Creating product: {}", request.name());

        var command = new CreateProductCommand(
                request.name(),
                request.description(),
                Money.of(request.price()),
                request.category(),
                request.stockQuantity());

        var product = createProductUseCase.create(command);

        log.info("Product created successfully with ID: {}", product.getId().value());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(product));
    }
}
