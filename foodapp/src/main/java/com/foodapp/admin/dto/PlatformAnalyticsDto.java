package com.foodapp.admin.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PlatformAnalyticsDto {
    private long totalUsers;
    private long totalCustomers;
    private long totalVendors;
    private long totalDeliveryAgents;
    private long totalRestaurants;
    private long totalOrders;
    private long activeOrders;
    private long deliveredOrders;
    private long cancelledOrders;
}
