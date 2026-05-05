package com.foodapp.order.dto;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PlaceOrderRequest {
    private Long restaurantId;
    private String deliveryAddress;
    private Double deliveryLat;
    private Double deliveryLng;
    private String specialInstructions;
    private String paymentMethod; // "UPI", "CARD", "COD"
    private List<OrderItemRequest> items;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class OrderItemRequest {
        private Long menuItemId;
        private int quantity;
    }
}
