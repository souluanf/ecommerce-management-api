package dev.luanfernandes.application.usecase.order;

import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListAllOrdersUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListAllOrdersUseCase.class);

    private final OrderRepository orderRepository;

    public ListAllOrdersUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

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
