package com.foodapp.delivery;

import com.foodapp.common.dto.ApiResponse;
import com.foodapp.delivery.dto.DeliveryAgentDto;
import com.foodapp.order.OrderStatus;
import com.foodapp.order.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DeliveryAgentDto>> register(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        DeliveryAgentDto dto = deliveryService.registerAsAgent(
                userDetails.getUsername(),
                request.get("vehicleType"),
                request.get("vehicleNumber")
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registered as delivery agent", dto));
    }

    @PatchMapping("/toggle-online")
    public ResponseEntity<ApiResponse<DeliveryAgentDto>> toggleOnline(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(deliveryService.toggleOnline(userDetails.getUsername())));
    }

    @PutMapping("/location")
    public ResponseEntity<ApiResponse<Void>> updateLocation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Double> request) {
        deliveryService.updateLocation(userDetails.getUsername(), request.get("lat"), request.get("lng"));
        return ResponseEntity.ok(ApiResponse.success("Location updated", null));
    }

    @PostMapping("/{orderId}/accept")
    public ResponseEntity<ApiResponse<OrderDto>> acceptDelivery(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(deliveryService.acceptDelivery(userDetails.getUsername(), orderId)));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderDto>> updateStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {
        OrderStatus status = OrderStatus.valueOf(request.get("status"));
        return ResponseEntity.ok(ApiResponse.success(
                deliveryService.updateDeliveryStatus(userDetails.getUsername(), orderId, status)));
    }
}
