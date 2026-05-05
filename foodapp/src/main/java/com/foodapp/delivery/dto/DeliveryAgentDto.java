package com.foodapp.delivery.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DeliveryAgentDto {
    private Long id;
    private String name;
    private String vehicleType;
    private String vehicleNumber;
    private boolean isOnline;
    private boolean isAvailable;
    private double avgRating;
    private int totalDeliveries;
}
