package com.foodapp.menu;

import com.foodapp.common.exception.*;
import com.foodapp.menu.dto.*;
import com.foodapp.restaurant.*;
import com.foodapp.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public List<MenuItemDto> getMenuByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<MenuItemDto> getAllMenuByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public MenuItemDto addMenuItem(String ownerEmail, Long restaurantId, CreateMenuItemRequest request) {
        Restaurant restaurant = verifyOwnership(ownerEmail, restaurantId);

        MenuItem item = MenuItem.builder()
                .restaurant(restaurant)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .category(request.getCategory())
                .isVegetarian(request.isVegetarian())
                .build();

        return toDto(menuItemRepository.save(item));
    }

    @Transactional
    public MenuItemDto updateMenuItem(String ownerEmail, Long restaurantId, Long itemId, CreateMenuItemRequest request) {
        verifyOwnership(ownerEmail, restaurantId);

        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", itemId));

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategory(request.getCategory());
        item.setVegetarian(request.isVegetarian());
        if (request.getImageUrl() != null) item.setImageUrl(request.getImageUrl());

        return toDto(menuItemRepository.save(item));
    }

    @Transactional
    public void deleteMenuItem(String ownerEmail, Long restaurantId, Long itemId) {
        verifyOwnership(ownerEmail, restaurantId);
        menuItemRepository.deleteById(itemId);
    }

    @Transactional
    public MenuItemDto toggleAvailability(String ownerEmail, Long restaurantId, Long itemId) {
        verifyOwnership(ownerEmail, restaurantId);
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", itemId));
        item.setAvailable(!item.isAvailable());
        return toDto(menuItemRepository.save(item));
    }

    private Restaurant verifyOwnership(String ownerEmail, Long restaurantId) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", ownerEmail));
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", restaurantId));
        if (!restaurant.getOwner().getId().equals(owner.getId())) {
            throw new BadRequestException("You can only manage your own restaurant's menu");
        }
        return restaurant;
    }

    private MenuItemDto toDto(MenuItem item) {
        return MenuItemDto.builder()
                .id(item.getId())
                .restaurantId(item.getRestaurant().getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .category(item.getCategory())
                .isVegetarian(item.isVegetarian())
                .isAvailable(item.isAvailable())
                .build();
    }
}
