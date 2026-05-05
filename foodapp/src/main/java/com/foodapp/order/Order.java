package com.foodapp.order;

import com.foodapp.delivery.DeliveryAgent;
import com.foodapp.restaurant.Restaurant;
import com.foodapp.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity — central to the platform.
 * Links customer, restaurant, delivery agent, and payment.
 * Uses snapshot pattern for delivery address (captured at order time).
 */
@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber; // e.g., "ORD-A1B2C3"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_agent_id")
    private DeliveryAgent deliveryAgent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PLACED;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Builder.Default
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    // Snapshot of delivery address at order time
    private String deliveryAddress;
    private Double deliveryLat;
    private Double deliveryLng;

    @Column(length = 500)
    private String specialInstructions;

    @CreationTimestamp
    private LocalDateTime placedAt;

    private LocalDateTime deliveredAt;

    private Integer estimatedDeliveryMins;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
}
