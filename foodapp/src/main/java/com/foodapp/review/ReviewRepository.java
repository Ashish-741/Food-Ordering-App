package com.foodapp.review;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);
    boolean existsByCustomerIdAndOrderId(Long customerId, Long orderId);
}
