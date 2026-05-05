package com.foodapp.address.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AddressRequest {
    private String label;  // "Home", "Work", "Other"
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private Double latitude;
    private Double longitude;
    private boolean isDefault;
}
