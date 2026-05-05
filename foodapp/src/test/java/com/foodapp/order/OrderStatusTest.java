package com.foodapp.order;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    @DisplayName("All order statuses exist")
    void allStatusesExist() {
        assertEquals(9, OrderStatus.values().length);
        assertNotNull(OrderStatus.valueOf("PLACED"));
        assertNotNull(OrderStatus.valueOf("CONFIRMED"));
        assertNotNull(OrderStatus.valueOf("PREPARING"));
        assertNotNull(OrderStatus.valueOf("READY"));
        assertNotNull(OrderStatus.valueOf("PICKED_UP"));
        assertNotNull(OrderStatus.valueOf("ON_THE_WAY"));
        assertNotNull(OrderStatus.valueOf("DELIVERED"));
        assertNotNull(OrderStatus.valueOf("CANCELLED"));
        assertNotNull(OrderStatus.valueOf("REJECTED"));
    }
}
