package com.foodapp.order;

/**
 * Order lifecycle state machine:
 *
 * PLACED → CONFIRMED → PREPARING → READY → PICKED_UP → ON_THE_WAY → DELIVERED
 *   ↓         ↓
 * CANCELLED  REJECTED
 */
public enum OrderStatus {
    PLACED,
    CONFIRMED,
    PREPARING,
    READY,
    PICKED_UP,
    ON_THE_WAY,
    DELIVERED,
    CANCELLED,
    REJECTED
}
