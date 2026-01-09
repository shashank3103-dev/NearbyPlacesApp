package com.nearbyplacesapp.modules;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.nearbyplacesapp.repository.PlacesRepository;
import com.nearbyplacesapp.models.Place;

import java.util.List;

public class LocationModule extends ReactContextBaseJavaModule {
    private static final String TAG = "LocationModule";
    private final ReactApplicationContext reactContext;
    private FusedLocationProviderClient fusedLocationClient;
    private PlacesRepository placesRepository;

    public LocationModule(ReactApplicationContext context) {
        super(context);
        this.reactContext = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.placesRepository = new PlacesRepository();
    }

    @NonNull
    @Override
    public String getName() {
        return "LocationModule";
    }

    @ReactMethod
    public void checkLocationPermission(Promise promise) {
        try {
            boolean hasPermission = ActivityCompat.checkSelfPermission(
                reactContext, 
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED;
            
            promise.resolve(hasPermission);
        } catch (Exception e) {
            promise.reject("PERMISSION_ERROR", e.getMessage());
        }
    }

    @ReactMethod
    public void getCurrentLocation(Promise promise) {
        if (ActivityCompat.checkSelfPermission(
                reactContext, 
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            promise.reject("PERMISSION_DENIED", "Location permission not granted");
            return;
        }

        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        WritableMap locationMap = Arguments.createMap();
                        locationMap.putDouble("latitude", location.getLatitude());
                        locationMap.putDouble("longitude", location.getLongitude());
                        locationMap.putDouble("accuracy", location.getAccuracy());
                        promise.resolve(locationMap);
                    } else {
                        promise.reject("LOCATION_NULL", "Unable to retrieve location");
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Location error: " + e.getMessage());
                    promise.reject("LOCATION_ERROR", e.getMessage());
                }
            });
    }

    @ReactMethod
    public void getNearbyPlaces(double latitude, double longitude, Promise promise) {
        // Run on background thread (async operation)
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // List places = placesRepository.fetchNearbyPlaces(latitude, longitude);
                    List<Place> places = placesRepository.fetchNearbyPlaces(latitude, longitude);

                    WritableArray placesArray = Arguments.createArray();
                    
                    for (Place place : places) {
                        WritableMap placeMap = Arguments.createMap();
                        placeMap.putString("id", place.getId());
                        placeMap.putString("name", place.getName());
                        placeMap.putDouble("latitude", place.getLatitude());
                        placeMap.putDouble("longitude", place.getLongitude());
                        placeMap.putDouble("distance", place.getDistance());
                        placeMap.putString("type", place.getType());
                        placesArray.pushMap(placeMap);
                    }
                    
                    promise.resolve(placesArray);
                } catch (Exception e) {
                    Log.e(TAG, "Places fetch error: " + e.getMessage());
                    promise.reject("FETCH_ERROR", e.getMessage());
                }
            }
        }).start();
    }
}