package com.foodapp.address.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AddressDto {
    private Long id;
    private String label;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private Double latitude;
    private Double longitude;
    private boolean isDefault;
}
