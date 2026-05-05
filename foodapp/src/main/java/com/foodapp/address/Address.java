package com.foodapp.address;

import com.foodapp.user.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Stores multiple addresses per user (Home, Work, etc.).
 * Latitude/longitude enable distance-based calculations (Haversine).
 */
@Entity
@Table(name = "addresses")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String label; // "Home", "Work", "Other"

    private String street;
    private String city;
    private String state;
    private String zipCode;

    private Double latitude;
    private Double longitude;

    @Builder.Default
    private boolean isDefault = false;
}
