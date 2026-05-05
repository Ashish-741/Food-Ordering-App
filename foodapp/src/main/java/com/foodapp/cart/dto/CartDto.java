package com.foodapp.cart.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CartDto {
    private Long restaurantId;
    private String restaurantName;
    private List<CartItemDto> items;
    private BigDecimal subtotal;
    private int totalItems;
}
