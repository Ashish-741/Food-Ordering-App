package com.foodapp.admin.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VendorAnalyticsDto {
    private Long restaurantId;
    private String restaurantName;
    private long totalOrders;
    private long completedOrders;
    private long cancelledOrders;
    private long activeOrders;
    private BigDecimal totalRevenue;
    private BigDecimal avgOrderValue;
    private double avgRating;
    private int totalReviews;
    private List<Map<String, Object>> recentOrders;  // Last 5 orders
    private Map<String, Long> ordersByStatus;         // Status breakdown
}
