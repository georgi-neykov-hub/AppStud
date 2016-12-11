package appstud.neykov.com.appstudassigment.map;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.neykov.mvp.RxPresenter;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import appstud.neykov.com.appstudassigment.model.GooglePlacesInteractor;
import appstud.neykov.com.appstudassigment.model.PlaceType;
import appstud.neykov.com.appstudassigment.model.Location;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlacesViewPresenter extends RxPresenter<PlacesView> {
    private static final int SEARCH_RADIUS_METERS = 2000;

    private GooglePlacesInteractor googlePlacesInteractor;
    private Subscription locationTrackingSubscription;

    @Inject
    public PlacesViewPresenter(GooglePlacesInteractor googlePlacesInteractor) {
        this.googlePlacesInteractor = googlePlacesInteractor;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTrackingLocation();
    }

    @UiThread
    public void startTrackingLocation() {
        if (locationTrackingSubscription != null) {
            return;
        }

        locationTrackingSubscription = googlePlacesInteractor.getLocationStream(500, 3, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(deliverLatest())
                .subscribe(delivery -> delivery.split(
                        PlacesView::displayLocation,
                        (mapView, throwable) -> {
                            stopTrackingLocation();
                            mapView.showError(PlacesView.ERROR_LOCATION_UPDATES_UNAVAILABLE, Bundle.EMPTY);
                        }));
    }

    @UiThread
    public void stopTrackingLocation() {
        if (locationTrackingSubscription != null) {
            locationTrackingSubscription.unsubscribe();
            locationTrackingSubscription = null;
        }
    }

    @UiThread
    public void loadNearbyBars(@NonNull Location location) {
        this.add(googlePlacesInteractor.getNearbyPlaces(PlaceType.BAR, location, SEARCH_RADIUS_METERS)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(deliver())
                .subscribe(delivery -> {
                    delivery.split(
                            (mapView, places) -> mapView.displayNearbyPlaces(location, places),
                            (mapView, throwable) -> mapView.showError(PlacesView.ERROR_LOADING_PLACES, Bundle.EMPTY));
                }));
    }
}
