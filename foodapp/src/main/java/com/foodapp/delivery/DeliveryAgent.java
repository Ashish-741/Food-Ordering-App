package com.foodapp.delivery;

import com.foodapp.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Delivery agent profile — one-to-one extension of User (role=DELIVERY).
 * Tracks real-time location for delivery assignment (Haversine distance matching).
 */
@Entity
@Table(name = "delivery_agents")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DeliveryAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String vehicleType; // "BIKE", "SCOOTER", "BICYCLE"
    private String vehicleNumber;

    @Builder.Default
    private boolean isAvailable = true; // not currently on a delivery

    @Builder.Default
    private boolean isOnline = false; // toggle by agent

    private Double currentLat;
    private Double currentLng;

    @Builder.Default
    private double avgRating = 0.0;

    @Builder.Default
    private int totalDeliveries = 0;

    @UpdateTimestamp
    private LocalDateTime lastLocationUpdate;
}
