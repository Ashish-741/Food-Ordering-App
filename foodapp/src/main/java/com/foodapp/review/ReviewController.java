package com.foodapp.review;

import com.foodapp.common.dto.ApiResponse;
import com.foodapp.review.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDto>> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateReviewRequest request) {
        ReviewDto dto = reviewService.createReview(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted", dto));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getRestaurantReviews(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getRestaurantReviews(restaurantId)));
    }
}
