package com.foodapp.restaurant;

import com.foodapp.admin.dto.VendorAnalyticsDto;
import com.foodapp.common.exception.ResourceNotFoundException;
import com.foodapp.order.*;
import com.foodapp.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Vendor analytics — provides revenue, order stats, and performance metrics
 * for the vendor dashboard.
 */
@Service
@RequiredArgsConstructor
public class VendorAnalyticsService {

    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public VendorAnalyticsDto getAnalytics(String vendorEmail) {
        User vendor = userRepository.findByEmail(vendorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", vendorEmail));

        Restaurant restaurant = restaurantRepository.findByOwnerId(vendor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "owner", vendorEmail));

        List<Order> allOrders = orderRepository.findByRestaurantIdOrderByPlacedAtDesc(restaurant.getId());

        long completed = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        long cancelled = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED || o.getStatus() == OrderStatus.REJECTED).count();
        long active = allOrders.stream().filter(o ->
                o.getStatus() != OrderStatus.DELIVERED &&
                o.getStatus() != OrderStatus.CANCELLED &&
                o.getStatus() != OrderStatus.REJECTED).count();

        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgOrderValue = completed > 0
                ? totalRevenue.divide(BigDecimal.valueOf(completed), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Order status breakdown
        Map<String, Long> statusBreakdown = allOrders.stream()
                .collect(Collectors.groupingBy(o -> o.getStatus().name(), Collectors.counting()));

        // Recent 5 orders
        List<Map<String, Object>> recentOrders = allOrders.stream()
                .limit(5)
                .map(o -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("orderNumber", o.getOrderNumber());
                    map.put("customerName", o.getCustomer().getName());
                    map.put("status", o.getStatus().name());
                    map.put("totalAmount", o.getTotalAmount());
                    map.put("placedAt", o.getPlacedAt() != null ? o.getPlacedAt().toString() : null);
                    return map;
                })
                .collect(Collectors.toList());

        return VendorAnalyticsDto.builder()
                .restaurantId(restaurant.getId())
                .restaurantName(restaurant.getName())
                .totalOrders(allOrders.size())
                .completedOrders(completed)
                .cancelledOrders(cancelled)
                .activeOrders(active)
                .totalRevenue(totalRevenue)
                .avgOrderValue(avgOrderValue)
                .avgRating(restaurant.getAvgRating())
                .totalReviews(restaurant.getTotalRatings())
                .recentOrders(recentOrders)
                .ordersByStatus(statusBreakdown)
                .build();
    }
}
