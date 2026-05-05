package com.foodapp.payment;

import com.foodapp.common.dto.ApiResponse;
import com.foodapp.payment.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Create a payment order after placing an order.
     * Body: { "orderId": 1, "paymentMethod": "UPI" }
     */
    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPaymentOrder(
            @RequestBody Map<String, Object> request) {
        Long orderId = Long.valueOf(request.get("orderId").toString());
        String paymentMethod = request.get("paymentMethod").toString();
        PaymentResponse response = paymentService.createPaymentOrder(orderId, paymentMethod);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment order created", response));
    }

    /**
     * Verify payment after Razorpay checkout completes.
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(
            @RequestBody PaymentVerifyRequest request) {
        PaymentResponse response = paymentService.verifyPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment verified", response));
    }

    /**
     * Check payment status for an order.
     */
    @GetMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentStatus(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentByOrderId(orderId)));
    }
}
