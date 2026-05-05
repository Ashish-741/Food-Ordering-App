package com.foodapp.common.util;

import java.util.UUID;

/**
 * Generates human-readable order numbers like "ORD-A1B2C3D4".
 */
public class OrderNumberGenerator {

    public static String generate() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ORD-" + uuid;
    }
}
