package com.foodapp.order.dto;

import com.foodapp.order.OrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private String orderNumber;
    private String customerName;
    private String restaurantName;
    private Long restaurantId;
    private String deliveryAgentName;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal tax;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private String specialInstructions;
    private LocalDateTime placedAt;
    private LocalDateTime deliveredAt;
    private Integer estimatedDeliveryMins;
    private List<OrderItemDto> items;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class OrderItemDto {
        private String itemName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}
