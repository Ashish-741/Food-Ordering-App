package com.foodapp.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * WebSocket STOMP controller for real-time order tracking.
 *
 * Client subscribes to:
 *   /topic/order/{orderId}              → Customer tracks their order
 *   /topic/vendor/{vendorId}/orders     → Vendor receives new orders
 *   /topic/delivery/{agentId}           → Delivery agent receives assignments
 *
 * Client sends to:
 *   /app/order/{orderId}/track          → Request current order status
 *   /app/delivery/location              → Delivery agent sends GPS update
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Customer requests the current tracking status of their order.
     * Client sends: STOMP SEND to /app/order/{orderId}/track
     * Server replies: broadcasts to /topic/order/{orderId}
     */
    @MessageMapping("/order/{orderId}/track")
    @SendTo("/topic/order/{orderId}")
    public Map<String, Object> trackOrder(@DestinationVariable Long orderId) {
        log.info("Tracking request for order: {}", orderId);
        return Map.of(
                "event", "TRACKING_UPDATE",
                "orderId", orderId,
                "message", "Tracking active for order " + orderId
        );
    }

    /**
     * Delivery agent sends real-time GPS location update.
     * Client sends: STOMP SEND to /app/delivery/location with body { orderId, lat, lng }
     * Server broadcasts location to the customer tracking the order.
     */
    @MessageMapping("/delivery/location")
    public void updateDeliveryLocation(Map<String, Object> payload) {
        Long orderId = Long.valueOf(payload.get("orderId").toString());
        Double lat = Double.valueOf(payload.get("lat").toString());
        Double lng = Double.valueOf(payload.get("lng").toString());

        log.debug("Delivery location update: order={}, lat={}, lng={}", orderId, lat, lng);

        // Broadcast to customer who is tracking this order
        messagingTemplate.convertAndSend("/topic/order/" + orderId, Map.of(
                "event", "LOCATION_UPDATE",
                "orderId", orderId,
                "lat", lat,
                "lng", lng
        ));
    }
}
