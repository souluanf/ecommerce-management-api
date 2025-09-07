package dev.luanfernandes.adapter.out.persistence.repository;

import dev.luanfernandes.adapter.out.persistence.entity.OrderJpaEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, String> {

    List<OrderJpaEntity> findByUserId(String userId);

    Page<OrderJpaEntity> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    long countByUserId(String userId);

    @Query("SELECT o FROM OrderJpaEntity o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<OrderJpaEntity> findByDateRange(
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM OrderJpaEntity o WHERE o.status = 'PAID' AND o.createdAt BETWEEN :startDate AND :endDate")
    List<OrderJpaEntity> findPaidOrdersByDateRange(
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(
            "SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END FROM OrderJpaEntity o JOIN o.items oi WHERE oi.productId = :productId")
    boolean existsByProductId(@Param("productId") String productId);

    @Query(
            value =
                    """
        SELECT
            u.id as userId,
            u.email as userEmail,
            SUM(o.total_amount) as totalSpent,
            COUNT(o.id) as orderCount
        FROM users u
        INNER JOIN orders o ON u.id = o.user_id
        WHERE o.status = 'PAID'
          AND (:startDate IS NULL OR o.created_at >= :startDate)
          AND (:endDate IS NULL OR o.created_at <= :endDate)
        GROUP BY u.id, u.email
        ORDER BY totalSpent DESC
        LIMIT :limit
        """,
            nativeQuery = true)
    List<Object[]> findTopUsersByTotalSpent(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("limit") int limit);

    @Query(
            value =
                    """
        SELECT
            u.id as userId,
            u.email as userEmail,
            AVG(o.total_amount) as averageTicket
        FROM users u
        INNER JOIN orders o ON u.id = o.user_id
        WHERE o.status = 'PAID'
          AND (:startDate IS NULL OR o.created_at >= :startDate)
          AND (:endDate IS NULL OR o.created_at <= :endDate)
        GROUP BY u.id, u.email
        ORDER BY averageTicket DESC
        """,
            nativeQuery = true)
    List<Object[]> findUserTicketAverage(
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(
            value =
                    """
        SELECT SUM(o.total_amount) as monthlyRevenue
        FROM orders o
        WHERE o.status = 'PAID'
          AND o.created_at BETWEEN :startDate AND :endDate
        """,
            nativeQuery = true)
    BigDecimal calculateMonthlyRevenue(
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
