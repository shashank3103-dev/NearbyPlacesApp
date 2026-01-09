import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  PermissionsAndroid,
  Platform,
  ActivityIndicator,
  TouchableOpacity,
} from 'react-native';
import MapView, { Marker, Callout, PROVIDER_GOOGLE } from 'react-native-maps';
import { NativeModules } from 'react-native';

/* ---------- TYPES ---------- */

type LocationType = {
  latitude: number;
  longitude: number;
};

type PlaceType = {
  id: string;
  name: string;
  latitude: number;
  longitude: number;
  distance: number;
  type: string;
};

type LocationModuleType = {
  getCurrentLocation: () => Promise<LocationType>;
  getNearbyPlaces: (lat: number, lng: number) => Promise<PlaceType[]>;
};

const { LocationModule } = NativeModules as {
  LocationModule: LocationModuleType;
};

/* ---------- COMPONENT ---------- */

const MapScreen: React.FC = () => {
  const [location, setLocation] = useState<LocationType | null>(null);
  const [places, setPlaces] = useState<PlaceType[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [selectedPlace, setSelectedPlace] = useState<PlaceType | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    requestLocationPermission();
  }, []);

  /* ---------- PERMISSIONS ---------- */

  const requestLocationPermission = async () => {
    try {
      if (Platform.OS === 'android') {
        const granted = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
        );

        if (granted === PermissionsAndroid.RESULTS.GRANTED) {
          fetchCurrentLocation();
        } else {
          setError('Location permission denied');
          setLoading(false);
        }
      } else {
        fetchCurrentLocation();
      }
    } catch {
      setError('Permission error');
      setLoading(false);
    }
  };

  /* ---------- LOCATION ---------- */

  const fetchCurrentLocation = async () => {
    try {
      const userLocation = await LocationModule.getCurrentLocation();
      setLocation(userLocation);
      fetchNearbyPlaces(userLocation.latitude, userLocation.longitude);
    } catch (err: any) {
      setError(err?.message ?? 'Unable to get location');
      setLoading(false);
    }
  };

  const fetchNearbyPlaces = async (lat: number, lng: number) => {
    try {
      const result = await LocationModule.getNearbyPlaces(lat, lng);
      setPlaces(result);
      setLoading(false);
    } catch {
      setError('Failed to fetch nearby places');
      setLoading(false);
    }
  };

  const handleRefresh = () => {
    setLoading(true);
    setError(null);
    setSelectedPlace(null);
    fetchCurrentLocation();
  };

  /* ---------- UI STATES ---------- */

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" />
        <Text style={styles.loadingText}>Finding your location...</Text>
      </View>
    );
  }

  if (error || !location) {
    return (
      <View style={styles.errorContainer}>
        <Text style={styles.errorText}>{error ?? 'Something went wrong'}</Text>
        <TouchableOpacity style={styles.retryButton} onPress={handleRefresh}>
          <Text style={styles.retryButtonText}>Retry</Text>
        </TouchableOpacity>
      </View>
    );
  }

  /* ---------- MAIN UI ---------- */

  return (
    <View style={styles.container}>
      <MapView
        provider={PROVIDER_GOOGLE}
        style={styles.map}
        showsUserLocation
        initialRegion={{
          latitude: location.latitude,
          longitude: location.longitude,
          latitudeDelta: 0.05,
          longitudeDelta: 0.05,
        }}
      >
        {places.map(place => (
          <Marker
            key={place.id}
            coordinate={{
              latitude: place.latitude,
              longitude: place.longitude,
            }}
            onPress={() => setSelectedPlace(place)}
          >
            <Callout>
              <View style={styles.calloutContainer}>
                <Text style={styles.calloutTitle}>{place.name}</Text>
                <Text style={styles.calloutDistance}>
                  {place.distance.toFixed(2)} km away
                </Text>
                <Text style={styles.calloutType}>{place.type}</Text>
              </View>
            </Callout>
          </Marker>
        ))}
      </MapView>

      {selectedPlace && (
        <View style={styles.infoCard}>
          <TouchableOpacity
            style={styles.closeButton}
            onPress={() => setSelectedPlace(null)}
          >
            <Text style={styles.closeButtonText}>✕</Text>
          </TouchableOpacity>

          <Text style={styles.infoTitle}>{selectedPlace.name}</Text>
          <Text style={styles.infoDistance}>
            📍 {selectedPlace.distance.toFixed(2)} km away
          </Text>
          <Text style={styles.infoType}>🏷️ {selectedPlace.type}</Text>
        </View>
      )}

      <TouchableOpacity style={styles.refreshButton} onPress={handleRefresh}>
        <Text style={styles.refreshButtonText}>🔄 Refresh</Text>
      </TouchableOpacity>
    </View>
  );
};

export default MapScreen;
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },

  map: {
    flex: 1,
  },

  /* ---------- LOADING ---------- */

  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5F5F5',
  },

  loadingText: {
    marginTop: 12,
    fontSize: 16,
    color: '#555',
  },

  /* ---------- ERROR ---------- */

  errorContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 24,
    backgroundColor: '#F5F5F5',
  },

  errorText: {
    fontSize: 16,
    color: '#D32F2F',
    textAlign: 'center',
    marginBottom: 16,
  },

  retryButton: {
    backgroundColor: '#007AFF',
    paddingHorizontal: 28,
    paddingVertical: 12,
    borderRadius: 8,
  },

  retryButtonText: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: '600',
  },

  /* ---------- MAP CALLOUT ---------- */

  calloutContainer: {
    backgroundColor: '#FFF',
    padding: 12,
    borderRadius: 10,
    minWidth: 180,
    elevation: 6,
  },

  calloutTitle: {
    fontSize: 16,
    fontWeight: '700',
    marginBottom: 4,
    color: '#333',
  },

  calloutDistance: {
    fontSize: 14,
    color: '#666',
  },

  calloutType: {
    fontSize: 12,
    color: '#007AFF',
    marginTop: 2,
  },

  /* ---------- INFO CARD ---------- */

  infoCard: {
    position: 'absolute',
    bottom: 24,
    left: 16,
    right: 16,
    backgroundColor: '#FFF',
    padding: 18,
    borderRadius: 14,
    elevation: 10,
    shadowColor: '#000',
    shadowOpacity: 0.25,
    shadowRadius: 6,
    shadowOffset: { width: 0, height: 3 },
  },

  closeButton: {
    position: 'absolute',
    top: 8,
    right: 8,
    padding: 6,
  },

  closeButtonText: {
    fontSize: 18,
    color: '#666',
  },

  infoTitle: {
    fontSize: 20,
    fontWeight: '700',
    marginBottom: 8,
    color: '#222',
  },

  infoDistance: {
    fontSize: 15,
    color: '#555',
    marginBottom: 4,
  },

  infoType: {
    fontSize: 15,
    color: '#007AFF',
    fontWeight: '500',
  },

  /* ---------- REFRESH BUTTON ---------- */

  refreshButton: {
    position: 'absolute',
    top: 20,
    right: 20,
    backgroundColor: '#FFF',
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 24,
    elevation: 6,
    shadowColor: '#000',
    shadowOpacity: 0.2,
    shadowRadius: 4,
    shadowOffset: { width: 0, height: 2 },
  },

  refreshButtonText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#007AFF',
  },
});

