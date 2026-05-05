package com.foodapp.restaurant;

import com.foodapp.common.dto.ApiResponse;
import com.foodapp.restaurant.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<ApiResponse<RestaurantDto>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateRestaurantRequest request) {
        RestaurantDto dto = restaurantService.createRestaurant(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Restaurant created", dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.getRestaurantById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantDto>>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String cuisine) {
        List<RestaurantDto> results;
        if (name != null) {
            results = restaurantService.searchByName(name);
        } else if (cuisine != null) {
            results = restaurantService.searchByCuisine(cuisine);
        } else {
            results = restaurantService.getAllActiveRestaurants();
        }
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<RestaurantDto>>> getNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radius) {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.findNearby(lat, lng, radius)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantDto>> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody CreateRestaurantRequest request) {
        RestaurantDto dto = restaurantService.updateRestaurant(userDetails.getUsername(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Restaurant updated", dto));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<RestaurantDto>> toggleOpenClose(
            @AuthenticationPrincipal UserDetails userDetails) {
        RestaurantDto dto = restaurantService.toggleOpenClose(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Store toggled", dto));
    }
}
