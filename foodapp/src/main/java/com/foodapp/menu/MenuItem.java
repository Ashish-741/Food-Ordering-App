package com.foodapp.menu;

import com.foodapp.restaurant.Restaurant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Menu item belonging to a restaurant.
 * Each restaurant manages its own menu independently (multi-vendor isolation).
 */
@Entity
@Table(name = "menu_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Positive
    @Column(nullable = false)
    private BigDecimal price;

    private String imageUrl;

    private String category; // "Starters", "Main Course", "Drinks", "Desserts"

    @Builder.Default
    private boolean isVegetarian = false;

    @Builder.Default
    private boolean isAvailable = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
