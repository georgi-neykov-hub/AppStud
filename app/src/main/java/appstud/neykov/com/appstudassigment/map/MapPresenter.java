package appstud.neykov.com.appstudassigment.map;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.neykov.mvp.RxPresenter;

import javax.inject.Inject;

import appstud.neykov.com.appstudassigment.model.GooglePlacesInteractor;
import appstud.neykov.com.appstudassigment.networking.places.Location;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Georgi on 12/10/2016.
 */

public class MapPresenter extends RxPresenter<MapView> {
    private static final int SEARCH_RADIUS_METERS = 2000;

    private GooglePlacesInteractor googlePlacesInteractor;

    @Inject
    public MapPresenter(GooglePlacesInteractor googlePlacesInteractor) {
        this.googlePlacesInteractor = googlePlacesInteractor;
    }

    public void loadNearbyBars(LatLng location) {
        this.add(googlePlacesInteractor.getNearbyBars(new Location(location.latitude, location.longitude), SEARCH_RADIUS_METERS)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(deliver())
                .subscribe(delivery -> {
                    delivery.split(
                            MapView::displayPlaces,
                            (mapView, throwable) -> mapView.showError(MapView.ERROR_LOADING_PLACES, Bundle.EMPTY));
                }));
    }
}
