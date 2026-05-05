package com.foodapp.user;

/**
 * Enum representing the roles available in the system.
 * Used for role-based access control (RBAC) with Spring Security.
 */
public enum Role {
    CUSTOMER,
    VENDOR,
    DELIVERY,
    ADMIN
}
