package appstud.neykov.com.appstudassigment.model;


import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.io.InputStream;

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
import retrofit2.Response;
import rx.Observable;

/**
 * Created by Georgi on 12/10/2016.
 */

public class GooglePlacesInteractor {
    private static final String PLACE_TYPE_BAR = "bar";

    private Provider<GooglePlacesApi> apiProvider;
    private String googleApiKey;

    @Inject
    public GooglePlacesInteractor(Provider<GooglePlacesApi> apiProvider, @GoogleApisToken String googleApiKey) {
        this.apiProvider = apiProvider;
        this.googleApiKey = googleApiKey;
    }

    public Observable<Place> getNearbyBars(Location location, int radiusMeters) {
        return Observable.create(subscriber -> {
            try {
                Response<PlacesSearchResponse> response = apiProvider.get()
                        .searchPlaces(googleApiKey, location, radiusMeters, PLACE_TYPE_BAR).execute();
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

    @WorkerThread
    public InputStream getPlacesPhoto(String photoReference, int desiredWidth, int desiredHeight) throws IOException, HttpException {
        Response response = apiProvider.get()
                .getPhoto(googleApiKey, photoReference, desiredWidth, desiredHeight)
                .execute();
        if (response.isSuccessful()) {
            return response.raw().body().byteStream();
        } else {
            throw new HttpException(response.code(), response.message());
        }
    }
}
