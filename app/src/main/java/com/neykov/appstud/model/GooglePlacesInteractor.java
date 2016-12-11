package com.neykov.appstud.model;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import com.neykov.appstud.networking.places.GooglePlacesApi;
import com.neykov.appstud.networking.HttpException;
import com.neykov.appstud.networking.places.GoogleApisKey;
import com.neykov.appstud.networking.places.PlacesApiException;
import com.neykov.appstud.networking.places.PlacesSearchResponse;
import com.neykov.appstud.networking.places.Status;
import com.neykov.appstud.util.Global;
import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by Georgi on 12/10/2016.
 */

public class GooglePlacesInteractor {
    public static final int GOOGLE_APIS_CONNECT_TIMEOUT_SECONDS = 15;

    private Context context;
    private Provider<GooglePlacesApi> apiProvider;
    private String googleApiKey;

    @Inject
    public GooglePlacesInteractor(@Global Context context, Provider<GooglePlacesApi> apiProvider, @GoogleApisKey String googleApiKey) {
        this.context = context;
        this.apiProvider = apiProvider;
        this.googleApiKey = googleApiKey;
    }

    public Observable<Place> getNearbyPlaces(@NonNull PlaceType type, @NonNull Location location, int radiusMeters) {
        return Observable.create(subscriber -> {
            try {
                Response<PlacesSearchResponse> response = apiProvider.get()
                        .searchPlaces(googleApiKey, location, radiusMeters, type.typeString()).execute();
                if (response.isSuccessful()) {
                    PlacesSearchResponse searchResponse = response.body();
                    if (searchResponse.getStatus() != Status.OK) {
                        throw new PlacesApiException(searchResponse.getStatus());
                    }
                    for (Place place : searchResponse.getResults()) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(place);
                        }
                    }
                } else {
                    throw new HttpException(response.code(), response.message());
                }

                subscriber.onCompleted();
            } catch (Exception e) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Location> getLocationStream(int distanceThresholdMeters, long updateInterval, TimeUnit timeUnit){
        return Observable.<Location>create(subscriber -> {
           GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                   .addApi(LocationServices.API)
                   .build();

            try {
                ConnectionResult connectionResult = googleApiClient.blockingConnect(GOOGLE_APIS_CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (connectionResult == null || !connectionResult.isSuccess()){
                    subscriber.onError(new IllegalStateException("Unable to connect to Play Services."));
                }

                LocationCallback locationListener = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        android.location.Location location = locationResult.getLastLocation();
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(new Location(location.getLatitude(), location.getLongitude()));
                        }
                    }
                };

                LocationRequest locationRequest = new LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                        .setInterval(timeUnit.toMillis(updateInterval))
                        .setSmallestDisplacement(distanceThresholdMeters);
                if (locationPermissionGranted()) {
                    //noinspection MissingPermission
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener, Looper.getMainLooper());
                    subscriber.add(Subscriptions.create(() -> {
                        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
                        googleApiClient.disconnect();
                    }));
                } else {
                    throw new IllegalStateException("Location permission not granted.");
                }
            } catch (Exception e) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(new LocationUnavailableException(e));
                }
                googleApiClient.disconnect();
            }
        }).subscribeOn(Schedulers.io());
    }

    private boolean locationPermissionGranted(){
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
