package com.foodapp.common.util;

/**
 * Haversine formula — calculates the distance between two GPS coordinates.
 * Used for:
 * - Finding nearby restaurants
 * - Assigning nearest delivery agent
 * - ETA calculation
 */
public class HaversineUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculate distance between two points in kilometers.
     *
     * @param lat1 Latitude of point 1
     * @param lon1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lon2 Longitude of point 2
     * @return Distance in kilometers
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Estimate delivery time based on distance.
     * Assumes average speed of 25 km/h in urban areas + 5 min buffer.
     */
    public static int estimateDeliveryMinutes(double distanceKm) {
        int travelMinutes = (int) Math.ceil((distanceKm / 25.0) * 60);
        return travelMinutes + 5; // 5 min buffer for pickup
    }
}
