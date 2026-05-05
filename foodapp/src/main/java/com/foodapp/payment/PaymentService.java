package com.foodapp.payment;

import com.foodapp.common.exception.*;
import com.foodapp.order.*;
import com.foodapp.payment.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment service — handles payment creation, verification, and status tracking.
 *
 * In production, this integrates with Razorpay/Stripe.
 * For development, it simulates payment flow:
 * 1. createPaymentOrder → generates a mock Razorpay order ID
 * 2. verifyPayment → simulates verification and marks as COMPLETED
 * 3. COD payments are auto-confirmed
 *
 * To integrate real Razorpay:
 * - Add razorpay-java SDK dependency
 * - Replace simulateRazorpayOrderCreation() with RazorpayClient.orders.create()
 * - Replace verifyPayment() with signature verification using HMAC-SHA256
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Step 1: Create a payment order (called after placing an order).
     * For COD, payment is created with status PENDING (collected on delivery).
     * For online payments, generates a Razorpay order ID.
     */
    @Transactional
    public PaymentResponse createPaymentOrder(Long orderId, String paymentMethod) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Check if payment already exists
        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            throw new BadRequestException("Payment already exists for this order");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentMethod(paymentMethod.toUpperCase())
                .build();

        if ("COD".equalsIgnoreCase(paymentMethod)) {
            payment.setPaymentStatus("CONFIRMED");
            payment.setTransactionId("COD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        } else {
            // Simulate Razorpay order creation
            String razorpayOrderId = simulateRazorpayOrderCreation(order.getTotalAmount());
            payment.setRazorpayOrderId(razorpayOrderId);
            payment.setPaymentStatus("PENDING");
        }

        payment = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(orderId)
                .orderNumber(order.getOrderNumber())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .transactionId(payment.getTransactionId())
                .build();
    }

    /**
     * Step 2: Verify payment (called from frontend after Razorpay checkout).
     * In production, verify the Razorpay signature using HMAC-SHA256.
     */
    @Transactional
    public PaymentResponse verifyPayment(PaymentVerifyRequest request) {
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "razorpayOrderId", request.getRazorpayOrderId()));

        // In production: verify signature with Razorpay secret
        // String generatedSignature = HmacUtils.hmacSha256Hex(secret, orderId + "|" + paymentId);
        // if (!generatedSignature.equals(request.getRazorpaySignature())) throw ...

        // For development: simulate successful verification
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setPaymentStatus("COMPLETED");
        payment.setPaidAt(LocalDateTime.now());
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        paymentRepository.save(payment);

        // Notify via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/order/" + payment.getOrder().getId(),
                java.util.Map.of("event", "PAYMENT_CONFIRMED", "orderId", payment.getOrder().getId())
        );

        log.info("Payment verified for order {}: {}", payment.getOrder().getOrderNumber(), payment.getTransactionId());

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .orderNumber(payment.getOrder().getOrderNumber())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .build();
    }

    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(orderId)
                .orderNumber(payment.getOrder().getOrderNumber())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .build();
    }

    /**
     * Simulates Razorpay order creation.
     * Replace this with actual Razorpay SDK call in production:
     *
     *   RazorpayClient client = new RazorpayClient(keyId, keySecret);
     *   JSONObject options = new JSONObject();
     *   options.put("amount", amount.multiply(100).intValue()); // paise
     *   options.put("currency", "INR");
     *   com.razorpay.Order razorpayOrder = client.orders.create(options);
     *   return razorpayOrder.get("id");
     */
    private String simulateRazorpayOrderCreation(BigDecimal amount) {
        String orderId = "order_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
        log.info("Simulated Razorpay order: {} for ₹{}", orderId, amount);
        return orderId;
    }
}
