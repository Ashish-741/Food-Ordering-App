package com.foodapp.restaurant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByOwnerId(Long ownerId);

    List<Restaurant> findByIsActiveTrueAndIsOpenTrue();

    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true AND " +
           "LOWER(r.cuisineType) LIKE LOWER(CONCAT('%', :cuisine, '%'))")
    List<Restaurant> findByCuisineType(@Param("cuisine") String cuisine);

    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true AND " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Restaurant> searchByName(@Param("name") String name);
}
