package com.foodapp.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CreateMenuItemRequest {
    @NotBlank(message = "Item name is required")
    private String name;
    private String description;
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    private String imageUrl;
    private String category;
    private boolean isVegetarian;
}
