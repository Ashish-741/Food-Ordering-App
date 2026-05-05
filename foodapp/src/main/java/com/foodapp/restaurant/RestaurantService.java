package com.foodapp.restaurant;

import com.foodapp.common.exception.*;
import com.foodapp.common.util.HaversineUtil;
import com.foodapp.restaurant.dto.*;
import com.foodapp.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    @Transactional
    public RestaurantDto createRestaurant(String ownerEmail, CreateRestaurantRequest request) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", ownerEmail));

        if (owner.getRole() != Role.VENDOR) {
            throw new BadRequestException("Only vendors can create restaurants");
        }

        if (restaurantRepository.findByOwnerId(owner.getId()).isPresent()) {
            throw new BadRequestException("You already have a restaurant registered");
        }

        Restaurant restaurant = Restaurant.builder()
                .owner(owner)
                .name(request.getName())
                .description(request.getDescription())
                .cuisineType(request.getCuisineType())
                .phone(request.getPhone())
                .imageUrl(request.getImageUrl())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .minOrderAmount(request.getMinOrderAmount() != null ? request.getMinOrderAmount() : java.math.BigDecimal.ZERO)
                .deliveryFee(request.getDeliveryFee() != null ? request.getDeliveryFee() : java.math.BigDecimal.valueOf(30))
                .avgDeliveryTimeMins(request.getAvgDeliveryTimeMins() != null ? request.getAvgDeliveryTimeMins() : 30)
                .build();

        restaurant = restaurantRepository.save(restaurant);
        return toDto(restaurant);
    }

    public RestaurantDto getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));
        return toDto(restaurant);
    }

    public List<RestaurantDto> getAllActiveRestaurants() {
        return restaurantRepository.findByIsActiveTrueAndIsOpenTrue()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<RestaurantDto> searchByName(String name) {
        return restaurantRepository.searchByName(name)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<RestaurantDto> searchByCuisine(String cuisine) {
        return restaurantRepository.findByCuisineType(cuisine)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Find restaurants within a radius (in km) using Haversine formula.
     */
    public List<RestaurantDto> findNearby(double lat, double lng, double radiusKm) {
        return restaurantRepository.findByIsActiveTrueAndIsOpenTrue().stream()
                .filter(r -> r.getLatitude() != null && r.getLongitude() != null)
                .filter(r -> HaversineUtil.calculateDistance(lat, lng, r.getLatitude(), r.getLongitude()) <= radiusKm)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RestaurantDto toggleOpenClose(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", ownerEmail));
        Restaurant restaurant = restaurantRepository.findByOwnerId(owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "owner", ownerEmail));
        restaurant.setOpen(!restaurant.isOpen());
        return toDto(restaurantRepository.save(restaurant));
    }

    @Transactional
    public RestaurantDto updateRestaurant(String ownerEmail, Long id, CreateRestaurantRequest request) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", ownerEmail));
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));

        if (!restaurant.getOwner().getId().equals(owner.getId())) {
            throw new BadRequestException("You can only update your own restaurant");
        }

        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setPhone(request.getPhone());
        restaurant.setAddress(request.getAddress());
        if (request.getImageUrl() != null) restaurant.setImageUrl(request.getImageUrl());
        if (request.getLatitude() != null) restaurant.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) restaurant.setLongitude(request.getLongitude());
        if (request.getMinOrderAmount() != null) restaurant.setMinOrderAmount(request.getMinOrderAmount());
        if (request.getDeliveryFee() != null) restaurant.setDeliveryFee(request.getDeliveryFee());
        if (request.getAvgDeliveryTimeMins() != null) restaurant.setAvgDeliveryTimeMins(request.getAvgDeliveryTimeMins());

        return toDto(restaurantRepository.save(restaurant));
    }

    private RestaurantDto toDto(Restaurant r) {
        return RestaurantDto.builder()
                .id(r.getId())
                .ownerName(r.getOwner().getName())
                .name(r.getName())
                .description(r.getDescription())
                .cuisineType(r.getCuisineType())
                .phone(r.getPhone())
                .imageUrl(r.getImageUrl())
                .address(r.getAddress())
                .latitude(r.getLatitude())
                .longitude(r.getLongitude())
                .isOpen(r.isOpen())
                .avgRating(r.getAvgRating())
                .totalRatings(r.getTotalRatings())
                .minOrderAmount(r.getMinOrderAmount())
                .deliveryFee(r.getDeliveryFee())
                .avgDeliveryTimeMins(r.getAvgDeliveryTimeMins())
                .build();
    }
}
