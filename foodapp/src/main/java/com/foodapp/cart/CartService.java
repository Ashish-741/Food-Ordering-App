package com.foodapp.cart;

import com.foodapp.cart.dto.*;
import com.foodapp.common.exception.*;
import com.foodapp.menu.*;
import com.foodapp.restaurant.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Cart service — in-memory implementation using ConcurrentHashMap.
 *
 * Design decisions:
 * - Uses ConcurrentHashMap for thread safety (keyed by user email)
 * - Each user can only have items from ONE restaurant at a time
 *   (like Swiggy — adding from another restaurant clears the cart)
 * - Can be swapped to Redis by implementing the same interface
 *
 * Cart structure: Map<email, Map<menuItemId, quantity>>
 * Restaurant tracking: Map<email, restaurantId>
 */
@Service
@RequiredArgsConstructor
public class CartService {

    // In-memory cart store (swap with Redis for production)
    private final Map<String, Map<Long, Integer>> carts = new ConcurrentHashMap<>();
    private final Map<String, Long> cartRestaurants = new ConcurrentHashMap<>();

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public CartDto getCart(String email) {
        Map<Long, Integer> cartItems = carts.getOrDefault(email, Collections.emptyMap());
        Long restaurantId = cartRestaurants.get(email);

        if (cartItems.isEmpty() || restaurantId == null) {
            return CartDto.builder()
                    .items(Collections.emptyList())
                    .subtotal(BigDecimal.ZERO)
                    .totalItems(0)
                    .build();
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);

        List<CartItemDto> items = cartItems.entrySet().stream()
                .map(entry -> {
                    MenuItem menuItem = menuItemRepository.findById(entry.getKey()).orElse(null);
                    if (menuItem == null) return null;
                    BigDecimal totalPrice = menuItem.getPrice().multiply(BigDecimal.valueOf(entry.getValue()));
                    return CartItemDto.builder()
                            .menuItemId(menuItem.getId())
                            .itemName(menuItem.getName())
                            .unitPrice(menuItem.getPrice())
                            .quantity(entry.getValue())
                            .totalPrice(totalPrice)
                            .imageUrl(menuItem.getImageUrl())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        BigDecimal subtotal = items.stream()
                .map(CartItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream().mapToInt(CartItemDto::getQuantity).sum();

        return CartDto.builder()
                .restaurantId(restaurantId)
                .restaurantName(restaurant != null ? restaurant.getName() : null)
                .items(items)
                .subtotal(subtotal)
                .totalItems(totalItems)
                .build();
    }

    public CartDto addToCart(String email, AddToCartRequest request) {
        Long currentRestaurant = cartRestaurants.get(email);

        // If adding from a different restaurant, clear the old cart
        if (currentRestaurant != null && !currentRestaurant.equals(request.getRestaurantId())) {
            carts.remove(email);
            cartRestaurants.remove(email);
        }

        // Validate menu item exists and belongs to the restaurant
        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", request.getMenuItemId()));

        if (!menuItem.getRestaurant().getId().equals(request.getRestaurantId())) {
            throw new BadRequestException("Menu item does not belong to this restaurant");
        }

        if (!menuItem.isAvailable()) {
            throw new BadRequestException("Item '" + menuItem.getName() + "' is currently unavailable");
        }

        Map<Long, Integer> cartItems = carts.computeIfAbsent(email, k -> new ConcurrentHashMap<>());
        cartItems.merge(request.getMenuItemId(), request.getQuantity(), Integer::sum);
        cartRestaurants.put(email, request.getRestaurantId());

        return getCart(email);
    }

    public CartDto updateQuantity(String email, Long menuItemId, int quantity) {
        Map<Long, Integer> cartItems = carts.get(email);
        if (cartItems == null) {
            throw new BadRequestException("Cart is empty");
        }

        if (quantity <= 0) {
            cartItems.remove(menuItemId);
            if (cartItems.isEmpty()) {
                carts.remove(email);
                cartRestaurants.remove(email);
            }
        } else {
            cartItems.put(menuItemId, quantity);
        }

        return getCart(email);
    }

    public CartDto removeItem(String email, Long menuItemId) {
        return updateQuantity(email, menuItemId, 0);
    }

    public void clearCart(String email) {
        carts.remove(email);
        cartRestaurants.remove(email);
    }
}
