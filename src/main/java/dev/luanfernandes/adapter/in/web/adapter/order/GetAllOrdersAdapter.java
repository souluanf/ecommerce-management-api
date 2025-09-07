package dev.luanfernandes.adapter.in.web.adapter.order;

import dev.luanfernandes.adapter.in.web.port.order.GetAllOrdersPort;
import dev.luanfernandes.application.usecase.order.FindOrdersByUserUseCase;
import dev.luanfernandes.application.usecase.order.ListAllOrdersUseCase;
import dev.luanfernandes.domain.dto.OrderResponse;
import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.valueobject.UserId;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GetAllOrdersAdapter implements GetAllOrdersPort {

    private final FindOrdersByUserUseCase findOrdersByUserUseCase;
    private final ListAllOrdersUseCase listAllOrdersUseCase;

    @Override
    public ResponseEntity<PageResponse<OrderResponse>> getAllOrders(
            UUID userId, int pageNumber, int pageSize, LocalDate startDate, LocalDate endDate, String sort) {

        log.debug("Fetching orders with pagination - user: {}, page: {}, size: {}", userId, pageNumber, pageSize);

        var pageRequest = PageRequest.of(pageNumber, pageSize, startDate, endDate, sort);

        PageResponse<OrderResponse> response;

        if (userId != null) {
            var userIdValue = UserId.of(userId);
            var pagedOrders = findOrdersByUserUseCase.execute(userIdValue, pageRequest);

            var responseContent =
                    pagedOrders.content().stream().map(OrderResponse::from).toList();

            response = PageResponse.of(
                    pagedOrders.pageNumber(), pagedOrders.pageSize(), pagedOrders.elements(), responseContent);
        } else {
            var pagedOrders = listAllOrdersUseCase.execute(pageRequest);

            var responseContent =
                    pagedOrders.content().stream().map(OrderResponse::from).toList();

            response = PageResponse.of(
                    pagedOrders.pageNumber(), pagedOrders.pageSize(), pagedOrders.elements(), responseContent);
        }

        return ResponseEntity.ok(response);
    }
}
