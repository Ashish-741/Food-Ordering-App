package com.foodapp.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CreateRestaurantRequest {
    @NotBlank(message = "Restaurant name is required")
    private String name;
    private String description;
    @NotBlank(message = "Cuisine type is required")
    private String cuisineType;
    private String phone;
    private String imageUrl;
    @NotBlank(message = "Address is required")
    private String address;
    private Double latitude;
    private Double longitude;
    private BigDecimal minOrderAmount;
    private BigDecimal deliveryFee;
    private Integer avgDeliveryTimeMins;
}
