package dev.luanfernandes.application.usecase.order;

import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListAllOrdersUseCase {

    private final OrderRepository orderRepository;

    public List<OrderDomain> execute() {
        log.info("ListAllOrders: Searching for all orders");
        var result = orderRepository.findAll();
        log.info("ListAllOrders: Found {} total orders", result.size());
        return result;
    }

    public PageResponse<OrderDomain> execute(PageRequest pageRequest) {
        log.info(
                "ListAllOrders: Searching for orders with pagination - page: {}, size: {}",
                pageRequest.pageNumber(),
                pageRequest.pageSize());

        var result = orderRepository.findAllPaginated(pageRequest);

        log.info(
                "ListAllOrders: Found {} orders on page {} of {}",
                result.content().size(),
                pageRequest.pageNumber(),
                result.getTotalPages());

        return result;
    }
}
