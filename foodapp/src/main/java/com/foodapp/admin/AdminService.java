package com.foodapp.admin;

import com.foodapp.admin.dto.PlatformAnalyticsDto;
import com.foodapp.order.*;
import com.foodapp.restaurant.RestaurantRepository;
import com.foodapp.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    public PlatformAnalyticsDto getAnalytics() {
        long totalUsers = userRepository.count();
        long totalRestaurants = restaurantRepository.count();
        long totalOrders = orderRepository.count();

        // Count by role
        long customers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CUSTOMER).count();
        long vendors = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.VENDOR).count();
        long agents = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.DELIVERY).count();

        // Count by order status
        long active = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != OrderStatus.DELIVERED
                        && o.getStatus() != OrderStatus.CANCELLED
                        && o.getStatus() != OrderStatus.REJECTED)
                .count();
        long delivered = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        long cancelled = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();

        return PlatformAnalyticsDto.builder()
                .totalUsers(totalUsers)
                .totalCustomers(customers)
                .totalVendors(vendors)
                .totalDeliveryAgents(agents)
                .totalRestaurants(totalRestaurants)
                .totalOrders(totalOrders)
                .activeOrders(active)
                .deliveredOrders(delivered)
                .cancelledOrders(cancelled)
                .build();
    }

    public void toggleUserActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.foodapp.common.exception.ResourceNotFoundException("User", "id", userId));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }
}
