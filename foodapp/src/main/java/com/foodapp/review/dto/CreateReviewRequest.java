package com.foodapp.review.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CreateReviewRequest {
    private Long orderId;
    private Long restaurantId;
    @Min(1) @Max(5)
    private int rating;
    private String comment;
}
