package com.foodapp.order;

import com.foodapp.common.exception.*;
import com.foodapp.common.util.*;
import com.foodapp.delivery.*;
import com.foodapp.menu.*;
import com.foodapp.order.dto.*;
import com.foodapp.restaurant.*;
import com.foodapp.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Order service — the core of the platform.
 *
 * Key flows:
 * 1. placeOrder: validates items, calculates totals, saves order, notifies vendor via WebSocket
 * 2. updateStatus: vendor confirms/rejects, triggers delivery assignment
 * 3. assignDeliveryAgent: finds nearest available agent using Haversine
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public OrderDto placeOrder(String customerEmail, PlaceOrderRequest request) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", customerEmail));

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", request.getRestaurantId()));

        if (!restaurant.isOpen()) {
            throw new BadRequestException("Restaurant is currently closed");
        }

        // Build order items and calculate totals
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (PlaceOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemReq.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", itemReq.getMenuItemId()));

            if (!menuItem.isAvailable()) {
                throw new BadRequestException("Item '" + menuItem.getName() + "' is not available");
            }

            BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            subtotal = subtotal.add(itemTotal);

            orderItems.add(OrderItem.builder()
                    .menuItem(menuItem)
                    .itemName(menuItem.getName())        // Snapshot
                    .unitPrice(menuItem.getPrice())      // Snapshot
                    .quantity(itemReq.getQuantity())
                    .totalPrice(itemTotal)
                    .build());
        }

        // Check minimum order amount
        if (restaurant.getMinOrderAmount() != null && subtotal.compareTo(restaurant.getMinOrderAmount()) < 0) {
            throw new BadRequestException("Minimum order amount is ₹" + restaurant.getMinOrderAmount());
        }

        BigDecimal deliveryFee = restaurant.getDeliveryFee();
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.05)); // 5% GST
        BigDecimal totalAmount = subtotal.add(deliveryFee).add(tax);

        // Calculate ETA
        int eta = restaurant.getAvgDeliveryTimeMins();
        if (request.getDeliveryLat() != null && restaurant.getLatitude() != null) {
            double distance = HaversineUtil.calculateDistance(
                    restaurant.getLatitude(), restaurant.getLongitude(),
                    request.getDeliveryLat(), request.getDeliveryLng()
            );
            eta = HaversineUtil.estimateDeliveryMinutes(distance) + 15; // +15 for prep
        }

        Order order = Order.builder()
                .orderNumber(OrderNumberGenerator.generate())
                .customer(customer)
                .restaurant(restaurant)
                .status(OrderStatus.PLACED)
                .subtotal(subtotal)
                .deliveryFee(deliveryFee)
                .tax(tax)
                .totalAmount(totalAmount)
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryLat(request.getDeliveryLat())
                .deliveryLng(request.getDeliveryLng())
                .specialInstructions(request.getSpecialInstructions())
                .estimatedDeliveryMins(eta)
                .build();

        // Link items to order
        orderItems.forEach(item -> item.setOrder(order));
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // 🔔 Notify vendor via WebSocket
        OrderDto dto = toDto(savedOrder);
        messagingTemplate.convertAndSend(
                "/topic/vendor/" + restaurant.getId() + "/orders", dto
        );

        return dto;
    }

    public List<OrderDto> getCustomerOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return orderRepository.findByCustomerIdOrderByPlacedAtDesc(user.getId())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<OrderDto> getVendorOrders(String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        Restaurant restaurant = restaurantRepository.findByOwnerId(owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "owner", email));
        return orderRepository.findByRestaurantIdOrderByPlacedAtDesc(restaurant.getId())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return toDto(order);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatus newStatus, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Validate state transition
        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);

        // Auto-assign delivery agent when order is READY
        if (newStatus == OrderStatus.READY && order.getDeliveryAgent() == null) {
            assignDeliveryAgent(order);
        }

        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        Order saved = orderRepository.save(order);
        OrderDto dto = toDto(saved);

        // 🔔 Notify customer via WebSocket
        messagingTemplate.convertAndSend("/topic/order/" + orderId, dto);

        return dto;
    }

    /**
     * Delivery assignment engine — finds the nearest available delivery agent
     * using the Haversine formula.
     */
    private void assignDeliveryAgent(Order order) {
        List<DeliveryAgent> availableAgents = deliveryAgentRepository.findByIsOnlineTrueAndIsAvailableTrue();

        if (availableAgents.isEmpty()) return;

        double restaurantLat = order.getRestaurant().getLatitude() != null ? order.getRestaurant().getLatitude() : 0;
        double restaurantLng = order.getRestaurant().getLongitude() != null ? order.getRestaurant().getLongitude() : 0;

        DeliveryAgent nearest = availableAgents.stream()
                .filter(a -> a.getCurrentLat() != null && a.getCurrentLng() != null)
                .min(Comparator.comparingDouble(a ->
                        HaversineUtil.calculateDistance(restaurantLat, restaurantLng, a.getCurrentLat(), a.getCurrentLng())
                ))
                .orElse(availableAgents.get(0)); // Fallback to first available

        nearest.setAvailable(false);
        deliveryAgentRepository.save(nearest);

        order.setDeliveryAgent(nearest);

        // 🔔 Notify delivery agent
        messagingTemplate.convertAndSend(
                "/topic/delivery/" + nearest.getId(), toDto(order)
        );
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        Map<OrderStatus, Set<OrderStatus>> validTransitions = Map.of(
                OrderStatus.PLACED, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED, OrderStatus.REJECTED),
                OrderStatus.CONFIRMED, Set.of(OrderStatus.PREPARING, OrderStatus.CANCELLED),
                OrderStatus.PREPARING, Set.of(OrderStatus.READY),
                OrderStatus.READY, Set.of(OrderStatus.PICKED_UP),
                OrderStatus.PICKED_UP, Set.of(OrderStatus.ON_THE_WAY),
                OrderStatus.ON_THE_WAY, Set.of(OrderStatus.DELIVERED)
        );

        Set<OrderStatus> allowed = validTransitions.getOrDefault(current, Set.of());
        if (!allowed.contains(next)) {
            throw new BadRequestException("Cannot transition from " + current + " to " + next);
        }
    }

    private OrderDto toDto(Order order) {
        List<OrderDto.OrderItemDto> items = order.getItems().stream()
                .map(i -> OrderDto.OrderItemDto.builder()
                        .itemName(i.getItemName())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .totalPrice(i.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomer().getName())
                .restaurantName(order.getRestaurant().getName())
                .restaurantId(order.getRestaurant().getId())
                .deliveryAgentName(order.getDeliveryAgent() != null ? order.getDeliveryAgent().getUser().getName() : null)
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .deliveryFee(order.getDeliveryFee())
                .tax(order.getTax())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .specialInstructions(order.getSpecialInstructions())
                .placedAt(order.getPlacedAt())
                .deliveredAt(order.getDeliveredAt())
                .estimatedDeliveryMins(order.getEstimatedDeliveryMins())
                .items(items)
                .build();
    }
}
