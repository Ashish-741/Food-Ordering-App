package com.foodapp.cart.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AddToCartRequest {
    private Long restaurantId;
    private Long menuItemId;
    private int quantity;
}
