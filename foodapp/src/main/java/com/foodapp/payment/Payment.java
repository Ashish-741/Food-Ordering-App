package com.foodapp.payment;

import com.foodapp.order.Order;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment entity — tracks payment method, status, and Razorpay/Stripe transaction details.
 */
@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private String paymentMethod; // "UPI", "CARD", "COD", "WALLET"

    @Builder.Default
    private String paymentStatus = "PENDING"; // PENDING, COMPLETED, FAILED, REFUNDED

    private String transactionId;

    @Column(nullable = false)
    private BigDecimal amount;

    // Razorpay-specific fields
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;
}
