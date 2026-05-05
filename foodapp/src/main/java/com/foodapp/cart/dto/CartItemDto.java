package com.foodapp.cart.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CartItemDto {
    private Long menuItemId;
    private String itemName;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal totalPrice;
    private String imageUrl;
}
