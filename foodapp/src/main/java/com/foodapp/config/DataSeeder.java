package com.foodapp.config;

import com.foodapp.delivery.DeliveryAgent;
import com.foodapp.delivery.DeliveryAgentRepository;
import com.foodapp.menu.MenuItem;
import com.foodapp.menu.MenuItemRepository;
import com.foodapp.restaurant.Restaurant;
import com.foodapp.restaurant.RestaurantRepository;
import com.foodapp.user.Role;
import com.foodapp.user.User;
import com.foodapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds the database with sample data on startup.
 * Only runs if no users exist (prevents duplicate inserts on restart).
 *
 * Creates:
 * - 1 Admin user
 * - 2 Vendor users with restaurants and menu items
 * - 2 Customer users
 * - 2 Delivery agents
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded, skipping...");
            return;
        }

        log.info("🌱 Seeding database with sample data...");

        // ===== ADMIN =====
        User admin = userRepository.save(User.builder()
                .name("Admin User")
                .email("admin@foodapp.com")
                .password(passwordEncoder.encode("admin123"))
                .phone("9000000001")
                .role(Role.ADMIN)
                .build());

        // ===== CUSTOMERS =====
        User customer1 = userRepository.save(User.builder()
                .name("Rahul Sharma")
                .email("rahul@gmail.com")
                .password(passwordEncoder.encode("password"))
                .phone("9876543210")
                .role(Role.CUSTOMER)
                .build());

        User customer2 = userRepository.save(User.builder()
                .name("Priya Patel")
                .email("priya@gmail.com")
                .password(passwordEncoder.encode("password"))
                .phone("9876543211")
                .role(Role.CUSTOMER)
                .build());

        // ===== VENDOR 1: Spice Garden =====
        User vendor1 = userRepository.save(User.builder()
                .name("Rajesh Kumar")
                .email("rajesh@vendor.com")
                .password(passwordEncoder.encode("vendor123"))
                .phone("9800000001")
                .role(Role.VENDOR)
                .build());

        Restaurant restaurant1 = restaurantRepository.save(Restaurant.builder()
                .owner(vendor1)
                .name("Spice Garden")
                .description("Authentic North Indian cuisine with rich flavors and aromatic spices")
                .cuisineType("North Indian, Mughlai")
                .phone("9800000001")
                .address("123 MG Road, Bengaluru")
                .latitude(12.9716)
                .longitude(77.5946)
                .isOpen(true)
                .avgRating(4.3)
                .totalRatings(150)
                .minOrderAmount(BigDecimal.valueOf(149))
                .deliveryFee(BigDecimal.valueOf(25))
                .avgDeliveryTimeMins(35)
                .build());

        // Spice Garden Menu
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant1)
                .name("Butter Chicken").description("Creamy tomato-based chicken curry")
                .price(BigDecimal.valueOf(299)).category("Main Course").isVegetarian(false).build());
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant1)
                .name("Paneer Tikka").description("Grilled cottage cheese with spices")
                .price(BigDecimal.valueOf(249)).category("Starters").isVegetarian(true).build());
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant1)
                .name("Dal Makhani").description("Rich black lentil curry with butter")
                .price(BigDecimal.valueOf(199)).category("Main Course").isVegetarian(true).build());
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant1)
                .name("Garlic Naan").description("Freshly baked bread with garlic")
                .price(BigDecimal.valueOf(49)).category("Breads").isVegetarian(true).build());
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant1)
                .name("Biryani (Chicken)").description("Fragrant basmati rice with chicken")
                .price(BigDecimal.valueOf(349)).category("Main Course").isVegetarian(false).build());
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant1)
                .name("Gulab Jamun").description("Sweet milk dumplings in sugar syrup")
                .price(BigDecimal.valueOf(99)).category("Desserts").isVegetarian(true).build());
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant1)
                .name("Mango Lassi").description("Thick yogurt drink with mango")
                .price(BigDecimal.valueOf(79)).category("Drinks").isVegetarian(true).build());

        // ===== VENDOR 2: Dragon Wok =====
        User vendor2 = userRepository.save(User.builder()
                .name("Li Wei")
                .email("liwei@vendor.com")
                .password(passwordEncoder.encode("vendor123"))
                .phone("9800000002")
                .role(Role.VENDOR)
                .build());

        Restaurant restaurant2 = restaurantRepository.save(Restaurant.builder()
                .owner(vendor2)
                .name("Dragon Wok")
                .description("Premium Chinese and Pan-Asian cuisine")
                .cuisineType("Chinese, Asian")
                .phone("9800000002")
                .address("456 Brigade Road, Bengaluru")
                .latitude(12.9722)
                .longitude(77.6070)
                .isOpen(true)
                .avgRating(4.5)
                .totalRatings(200)
                .minOrderAmount(BigDecimal.valueOf(199))
                .deliveryFee(BigDecimal.valueOf(30))
                .avgDeliveryTimeMins(30)
                .build());

        // Dragon Wok Menu
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant2)
                .name("Kung Pao Chicken").description("Spicy stir-fried chicken with peanuts")
                .price(BigDecimal.valueOf(329)).category("Main Course").isVegetarian(false).build());
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant2)
                .name("Veg Manchurian").description("Crispy vegetable balls in tangy sauce")
                .price(BigDecimal.valueOf(199)).category("Starters").isVegetarian(true).build());
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant2)
                .name("Hakka Noodles").description("Stir-fried noodles with vegetables")
                .price(BigDecimal.valueOf(179)).category("Main Course").isVegetarian(true).build());
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant2)
                .name("Dim Sum (6 pcs)").description("Steamed dumplings with mixed filling")
                .price(BigDecimal.valueOf(249)).category("Starters").isVegetarian(false).build());
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant2)
                .name("Fried Rice").description("Wok-tossed rice with vegetables and egg")
                .price(BigDecimal.valueOf(199)).category("Main Course").isVegetarian(false).build());
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant2)
                .name("Green Tea Ice Cream").description("Japanese-style matcha ice cream")
                .price(BigDecimal.valueOf(129)).category("Desserts").isVegetarian(true).build());

        // ===== DELIVERY AGENTS =====
        User deliveryUser1 = userRepository.save(User.builder()
                .name("Vikram Singh")
                .email("vikram@delivery.com")
                .password(passwordEncoder.encode("delivery123"))
                .phone("9700000001")
                .role(Role.DELIVERY)
                .build());

        deliveryAgentRepository.save(DeliveryAgent.builder()
                .user(deliveryUser1)
                .vehicleType("BIKE")
                .vehicleNumber("KA01AB1234")
                .isOnline(true)
                .isAvailable(true)
                .currentLat(12.9700)
                .currentLng(77.5950)
                .avgRating(4.6)
                .totalDeliveries(342)
                .build());

        User deliveryUser2 = userRepository.save(User.builder()
                .name("Amit Yadav")
                .email("amit@delivery.com")
                .password(passwordEncoder.encode("delivery123"))
                .phone("9700000002")
                .role(Role.DELIVERY)
                .build());

        deliveryAgentRepository.save(DeliveryAgent.builder()
                .user(deliveryUser2)
                .vehicleType("SCOOTER")
                .vehicleNumber("KA01CD5678")
                .isOnline(true)
                .isAvailable(true)
                .currentLat(12.9750)
                .currentLng(77.6000)
                .avgRating(4.4)
                .totalDeliveries(218)
                .build());

        log.info("✅ Database seeded successfully!");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("  Test Accounts:");
        log.info("  Admin:    admin@foodapp.com / admin123");
        log.info("  Customer: rahul@gmail.com / password");
        log.info("  Customer: priya@gmail.com / password");
        log.info("  Vendor:   rajesh@vendor.com / vendor123");
        log.info("  Vendor:   liwei@vendor.com / vendor123");
        log.info("  Delivery: vikram@delivery.com / delivery123");
        log.info("  Delivery: amit@delivery.com / delivery123");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
