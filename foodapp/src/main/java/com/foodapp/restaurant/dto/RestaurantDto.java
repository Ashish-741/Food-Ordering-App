package com.foodapp.restaurant.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RestaurantDto {
    private Long id;
    private String ownerName;
    private String name;
    private String description;
    private String cuisineType;
    private String phone;
    private String imageUrl;
    private String address;
    private Double latitude;
    private Double longitude;
    private boolean isOpen;
    private double avgRating;
    private int totalRatings;
    private BigDecimal minOrderAmount;
    private BigDecimal deliveryFee;
    private int avgDeliveryTimeMins;
}
