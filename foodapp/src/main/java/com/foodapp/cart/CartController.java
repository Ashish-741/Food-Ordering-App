package com.foodapp.cart;

import com.foodapp.cart.dto.*;
import com.foodapp.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(user.getUsername())));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDto>> addItem(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Item added to cart",
                cartService.addToCart(user.getUsername(), request)));
    }

    @PutMapping("/items/{menuItemId}")
    public ResponseEntity<ApiResponse<CartDto>> updateQuantity(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long menuItemId,
            @RequestBody Map<String, Integer> request) {
        return ResponseEntity.ok(ApiResponse.success(
                cartService.updateQuantity(user.getUsername(), menuItemId, request.get("quantity"))));
    }

    @DeleteMapping("/items/{menuItemId}")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long menuItemId) {
        return ResponseEntity.ok(ApiResponse.success("Item removed",
                cartService.removeItem(user.getUsername(), menuItemId)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal UserDetails user) {
        cartService.clearCart(user.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }
}
