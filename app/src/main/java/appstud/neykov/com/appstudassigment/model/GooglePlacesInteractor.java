package appstud.neykov.com.appstudassigment.model;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import appstud.neykov.com.appstudassigment.networking.places.GooglePlacesApi;
import appstud.neykov.com.appstudassigment.networking.HttpException;
import appstud.neykov.com.appstudassigment.networking.places.GoogleApisToken;
import appstud.neykov.com.appstudassigment.networking.places.Location;
import appstud.neykov.com.appstudassigment.networking.places.Place;
import appstud.neykov.com.appstudassigment.networking.places.PlacesApiException;
import appstud.neykov.com.appstudassigment.networking.places.PlacesSearchResponse;
import appstud.neykov.com.appstudassigment.networking.places.Status;
import appstud.neykov.com.appstudassigment.util.Global;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.RxThreadFactory;
import rx.plugins.RxJavaSchedulersHook;
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
    public GooglePlacesInteractor(@Global Context context, Provider<GooglePlacesApi> apiProvider, @GoogleApisToken String googleApiKey) {
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

    @WorkerThread
    public InputStream getPlacesPhoto(String photoReference, int desiredWidth, int desiredHeight) throws IOException, HttpException {
        Response<ResponseBody> response = apiProvider.get()
                .getPhoto(googleApiKey, photoReference, desiredWidth, desiredHeight)
                .execute();
        if (response.isSuccessful()) {
            return response.body().byteStream();
        } else {
            throw new HttpException(response.code(), response.message());
        }
    }

    private boolean locationPermissionGranted(){
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
