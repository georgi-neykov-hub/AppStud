package com.neykov.appstud;

import android.support.annotation.NonNull;

import java.util.List;

import com.neykov.appstud.base.ErrorDisplayView;
import com.neykov.appstud.model.Location;
import com.neykov.appstud.model.Place;

public interface PlacesView extends ErrorDisplayView {
    int ERROR_LOADING_PLACES = 1000;
    int ERROR_LOCATION_UPDATES_UNAVAILABLE = 1002;

    void displayLocation(@NonNull Location location);
    void displayNearbyPlaces(@NonNull Location location, @NonNull List<Place> places);
}
