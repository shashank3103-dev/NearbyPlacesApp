package com.nearbyplacesapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nearbyplacesapp.models.Place;
import com.nearbyplacesapp.repository.PlacesRepository;

import java.util.List;

public class LocationViewModel extends ViewModel {
    private PlacesRepository repository;
    private MutableLiveData<List> placesLiveData;
    private MutableLiveData errorLiveData;
    private MutableLiveData loadingLiveData;

    public LocationViewModel() {
        this.repository = new PlacesRepository();
        this.placesLiveData = new MutableLiveData<>();
        this.errorLiveData = new MutableLiveData<>();
        this.loadingLiveData = new MutableLiveData<>();
    }

    public LiveData<List> getPlaces() {
        return placesLiveData;
    }

    public LiveData getError() {
        return errorLiveData;
    }

    public LiveData getLoading() {
        return loadingLiveData;
    }

    public void fetchNearbyPlaces(final double latitude, final double longitude) {
        loadingLiveData.postValue(true);
        
        // Async operation on background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List places = repository.fetchNearbyPlaces(latitude, longitude);
                    placesLiveData.postValue(places);
                    errorLiveData.postValue(null);
                } catch (Exception e) {
                    errorLiveData.postValue(e.getMessage());
                } finally {
                    loadingLiveData.postValue(false);
                }
            }
        }).start();
    }

    public void clearError() {
        errorLiveData.setValue(null);
    }
}