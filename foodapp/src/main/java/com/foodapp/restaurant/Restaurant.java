package com.foodapp.restaurant;

import com.foodapp.menu.MenuItem;
import com.foodapp.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Restaurant entity — each vendor owns one restaurant.
 * Contains denormalized avg_rating for fast reads (updated on each new review).
 */
@Entity
@Table(name = "restaurants")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private User owner;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private String cuisineType; // "Indian, Chinese, Italian"
    private String phone;
    private String imageUrl;
    private String address;

    private Double latitude;
    private Double longitude;

    @Builder.Default
    private boolean isOpen = false;

    @Builder.Default
    private double avgRating = 0.0;

    @Builder.Default
    private int totalRatings = 0;

    @Builder.Default
    private BigDecimal minOrderAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal deliveryFee = BigDecimal.valueOf(30);

    @Builder.Default
    private int avgDeliveryTimeMins = 30;

    @Builder.Default
    private boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MenuItem> menuItems = new ArrayList<>();
}
