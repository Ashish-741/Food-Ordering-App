package com.foodapp.cart;

import com.foodapp.cart.dto.*;
import com.foodapp.common.exception.BadRequestException;
import com.foodapp.menu.*;
import com.foodapp.restaurant.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private MenuItemRepository menuItemRepository;
    @Mock private RestaurantRepository restaurantRepository;

    @InjectMocks
    private CartService cartService;

    private MenuItem createMenuItem(Long id, Long restaurantId, String name, double price) {
        Restaurant r = new Restaurant();
        r.setId(restaurantId);
        return MenuItem.builder()
                .id(id).restaurant(r).name(name)
                .price(BigDecimal.valueOf(price)).isAvailable(true)
                .build();
    }

    @Test
    @DisplayName("Empty cart returns zero items")
    void emptyCart() {
        CartDto cart = cartService.getCart("user@test.com");
        assertEquals(0, cart.getTotalItems());
        assertEquals(BigDecimal.ZERO, cart.getSubtotal());
    }

    @Test
    @DisplayName("Add item to cart increases count")
    void addItem_success() {
        MenuItem item = createMenuItem(1L, 1L, "Butter Chicken", 299);
        Restaurant r = new Restaurant();
        r.setId(1L);
        r.setName("Spice Garden");

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(r));

        AddToCartRequest request = new AddToCartRequest(1L, 1L, 2);
        CartDto cart = cartService.addToCart("user@test.com", request);

        assertEquals(2, cart.getTotalItems());
        assertEquals(0, BigDecimal.valueOf(598).compareTo(cart.getSubtotal()));
    }

    @Test
    @DisplayName("Adding from different restaurant clears cart")
    void addFromDifferentRestaurant_clearsOldCart() {
        // Add from restaurant 1
        MenuItem item1 = createMenuItem(1L, 1L, "Butter Chicken", 299);
        Restaurant r1 = new Restaurant();
        r1.setId(1L);
        r1.setName("Spice Garden");

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(r1));
        cartService.addToCart("user@test.com", new AddToCartRequest(1L, 1L, 2));

        // Add from restaurant 2 — should clear restaurant 1 items
        MenuItem item2 = createMenuItem(8L, 2L, "Kung Pao Chicken", 329);
        Restaurant r2 = new Restaurant();
        r2.setId(2L);
        r2.setName("Dragon Wok");

        when(menuItemRepository.findById(8L)).thenReturn(Optional.of(item2));
        when(restaurantRepository.findById(2L)).thenReturn(Optional.of(r2));
        CartDto cart = cartService.addToCart("user@test.com", new AddToCartRequest(2L, 8L, 1));

        assertEquals(1, cart.getTotalItems()); // Only new item
        assertEquals("Dragon Wok", cart.getRestaurantName());
    }

    @Test
    @DisplayName("Adding unavailable item throws exception")
    void addUnavailableItem_throws() {
        MenuItem item = createMenuItem(1L, 1L, "Sold Out Item", 100);
        item.setAvailable(false);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class,
                () -> cartService.addToCart("user@test.com", new AddToCartRequest(1L, 1L, 1)));
    }

    @Test
    @DisplayName("Clear cart removes all items")
    void clearCart_works() {
        MenuItem item = createMenuItem(1L, 1L, "Item", 100);
        Restaurant r = new Restaurant();
        r.setId(1L);
        r.setName("Test");

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(r));
        cartService.addToCart("user@test.com", new AddToCartRequest(1L, 1L, 3));

        cartService.clearCart("user@test.com");
        CartDto cart = cartService.getCart("user@test.com");
        assertEquals(0, cart.getTotalItems());
    }
}
