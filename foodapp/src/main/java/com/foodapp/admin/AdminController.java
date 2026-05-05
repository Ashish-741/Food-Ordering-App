package com.foodapp.admin;

import com.foodapp.admin.dto.PlatformAnalyticsDto;
import com.foodapp.common.dto.ApiResponse;
import com.foodapp.order.OrderRepository;
import com.foodapp.order.dto.OrderDto;
import com.foodapp.order.OrderService;
import com.foodapp.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<PlatformAnalyticsDto>> getAnalytics() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAnalytics()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(u -> UserDto.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .phone(u.getPhone())
                        .role(u.getRole())
                        .isActive(u.isActive())
                        .createdAt(u.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PutMapping("/users/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleUser(@PathVariable Long id) {
        adminService.toggleUserActive(id);
        return ResponseEntity.ok(ApiResponse.success("User toggled", null));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getAllOrders() {
        List<OrderDto> orders = orderRepository.findAll().stream()
                .map(o -> orderService.getOrderById(o.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
}
