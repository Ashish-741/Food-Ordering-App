package com.foodapp.common.util;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class HaversineUtilTest {

    @Test
    @DisplayName("Distance between same point should be 0")
    void samePoint_zeroDistance() {
        double distance = HaversineUtil.calculateDistance(12.9716, 77.5946, 12.9716, 77.5946);
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    @DisplayName("Distance between Bengaluru MG Road and Koramangala (~4.5 km)")
    void mgRoad_to_koramangala() {
        // MG Road: 12.9716, 77.5946
        // Koramangala: 12.9352, 77.6245
        double distance = HaversineUtil.calculateDistance(12.9716, 77.5946, 12.9352, 77.6245);
        assertTrue(distance > 3.0 && distance < 6.0,
                "Expected ~4.5 km, got " + distance);
    }

    @Test
    @DisplayName("Distance between Bengaluru and Mumbai (~840 km)")
    void bengaluru_to_mumbai() {
        double distance = HaversineUtil.calculateDistance(12.9716, 77.5946, 19.0760, 72.8777);
        assertTrue(distance > 800 && distance < 900,
                "Expected ~840 km, got " + distance);
    }

    @Test
    @DisplayName("ETA for 5 km should be reasonable")
    void eta_5km() {
        int eta = HaversineUtil.estimateDeliveryMinutes(5.0);
        // 5 km at 25 km/h = 12 min + 5 buffer = 17 min
        assertTrue(eta >= 15 && eta <= 20, "Expected ~17 min, got " + eta);
    }

    @Test
    @DisplayName("ETA for 0 km should include buffer")
    void eta_0km() {
        int eta = HaversineUtil.estimateDeliveryMinutes(0.0);
        assertEquals(5, eta); // Just the buffer
    }
}
