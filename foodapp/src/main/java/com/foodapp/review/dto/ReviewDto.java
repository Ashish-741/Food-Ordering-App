package com.foodapp.review.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private String customerName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
