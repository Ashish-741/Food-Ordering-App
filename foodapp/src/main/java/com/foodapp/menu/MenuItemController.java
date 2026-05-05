package com.foodapp.menu;

import com.foodapp.common.dto.ApiResponse;
import com.foodapp.menu.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menu")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuItemDto>>> getMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getMenuByRestaurant(restaurantId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MenuItemDto>> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateMenuItemRequest request) {
        MenuItemDto dto = menuItemService.addMenuItem(userDetails.getUsername(), restaurantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Menu item added", dto));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ApiResponse<MenuItemDto>> updateItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @Valid @RequestBody CreateMenuItemRequest request) {
        MenuItemDto dto = menuItemService.updateMenuItem(userDetails.getUsername(), restaurantId, itemId, request);
        return ResponseEntity.ok(ApiResponse.success("Menu item updated", dto));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long restaurantId,
            @PathVariable Long itemId) {
        menuItemService.deleteMenuItem(userDetails.getUsername(), restaurantId, itemId);
        return ResponseEntity.ok(ApiResponse.success("Menu item deleted", null));
    }

    @PatchMapping("/{itemId}/availability")
    public ResponseEntity<ApiResponse<MenuItemDto>> toggleAvailability(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long restaurantId,
            @PathVariable Long itemId) {
        MenuItemDto dto = menuItemService.toggleAvailability(userDetails.getUsername(), restaurantId, itemId);
        return ResponseEntity.ok(ApiResponse.success("Availability toggled", dto));
    }
}
