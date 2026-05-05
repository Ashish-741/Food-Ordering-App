package com.foodapp.order;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCustomerIdOrderByPlacedAtDesc(Long customerId);
    List<Order> findByRestaurantIdOrderByPlacedAtDesc(Long restaurantId);
    List<Order> findByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);
    List<Order> findByDeliveryAgentIdOrderByPlacedAtDesc(Long agentId);
}
