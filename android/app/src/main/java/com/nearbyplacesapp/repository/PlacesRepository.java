package com.nearbyplacesapp.repository;

import com.nearbyplacesapp.models.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlacesRepository {
    private static final String TAG = "PlacesRepository";
    
    private static final String[] PLACE_NAMES = {
        "Starbucks Coffee", "McDonald's", "Central Park", "Fitness First", 
        "City Library", "Westfield Mall", "AMC Cinema", "Shell Gas Station", 
        "General Hospital", "Lincoln High School", "Pizza Hut", "Subway",
        "Bank of America", "CVS Pharmacy", "Hilton Hotel"
    };
    
    private static final String[] PLACE_TYPES = {
        "cafe", "restaurant", "park", "gym", "library",
        "shopping", "entertainment", "service", "health", "education",
        "restaurant", "restaurant", "bank", "pharmacy", "hotel"
    };

    public List fetchNearbyPlaces(double latitude, double longitude) {
        // Mock data generation - Replace with real API call in production
        // Example: Google Places API, Foursquare API, etc.
        
        List places = new ArrayList<>();
        Random random = new Random();
        
        // Generate 10-15 random nearby places
        int placeCount = 10 + random.nextInt(6);
        
        for (int i = 0; i < placeCount; i++) {
            // Random offset within ~2km radius
            double latOffset = (random.nextDouble() - 0.5) * 0.02;
            double lngOffset = (random.nextDouble() - 0.5) * 0.02;
            
            double placeLat = latitude + latOffset;
            double placeLng = longitude + lngOffset;
            
            // Calculate distance
            double distance = calculateDistance(latitude, longitude, placeLat, placeLng);
            
            // Random place selection
            int index = random.nextInt(PLACE_NAMES.length);
            
            Place place = new Place(
                "place_" + i,
                PLACE_NAMES[index],
                placeLat,
                placeLng,
                distance,
                PLACE_TYPES[index]
            );
            
            places.add(place);
        }
        
        return places;
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     * @return distance in kilometers
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
}