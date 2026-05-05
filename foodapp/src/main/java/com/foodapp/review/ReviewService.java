package com.foodapp.review;

import com.foodapp.common.exception.*;
import com.foodapp.order.*;
import com.foodapp.restaurant.*;
import com.foodapp.review.dto.*;
import com.foodapp.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public ReviewDto createReview(String customerEmail, CreateReviewRequest request) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", customerEmail));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException("Can only review delivered orders");
        }

        if (reviewRepository.existsByCustomerIdAndOrderId(customer.getId(), order.getId())) {
            throw new BadRequestException("You have already reviewed this order");
        }

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", request.getRestaurantId()));

        Review review = Review.builder()
                .customer(customer)
                .restaurant(restaurant)
                .order(order)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        reviewRepository.save(review);

        // Update restaurant's average rating (weighted towards recent)
        updateRestaurantRating(restaurant);

        return toDto(review);
    }

    public List<ReviewDto> getRestaurantReviews(Long restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Weighted rating: recent reviews have more importance.
     * Simple approach: recalculate average from all reviews.
     */
    private void updateRestaurantRating(Restaurant restaurant) {
        List<Review> reviews = reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurant.getId());
        if (reviews.isEmpty()) return;

        double totalWeight = 0;
        double weightedSum = 0;
        int size = reviews.size();

        for (int i = 0; i < size; i++) {
            // More recent reviews get higher weight
            double weight = (double) (size - i) / size;
            weightedSum += reviews.get(i).getRating() * weight;
            totalWeight += weight;
        }

        restaurant.setAvgRating(Math.round((weightedSum / totalWeight) * 10.0) / 10.0);
        restaurant.setTotalRatings(size);
        restaurantRepository.save(restaurant);
    }

    private ReviewDto toDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .customerName(review.getCustomer().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
