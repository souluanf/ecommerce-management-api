package dev.luanfernandes.application.usecase.order;

import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import dev.luanfernandes.domain.valueobject.UserId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindOrdersByUserUseCase {

    private final OrderRepository orderRepository;

    public List<OrderDomain> execute(UserId userId) {
        log.info("FindOrdersByUser: Searching orders for user ID: {}", userId.value());

        var result = orderRepository.findByUserId(userId);

        log.info("FindOrdersByUser: Found {} orders for user ID: {}", result.size(), userId.value());
        result.forEach(order -> log.debug(
                "Order: ID={}, status={}, total={}",
                order.getId().value(),
                order.getStatus(),
                order.getTotalAmount().value()));

        return result;
    }

    public PageResponse<OrderDomain> execute(UserId userId, PageRequest pageRequest) {
        log.info(
                "FindOrdersByUser: Searching orders for user ID: {} with pagination - page: {}, size: {}",
                userId.value(),
                pageRequest.pageNumber(),
                pageRequest.pageSize());

        var result = orderRepository.findByUserIdPaginated(userId, pageRequest);

        log.info(
                "FindOrdersByUser: Found {} orders for user ID: {} on page {} of {}",
                result.content().size(),
                userId.value(),
                pageRequest.pageNumber(),
                result.getTotalPages());

        return result;
    }
}
