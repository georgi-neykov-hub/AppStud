package appstud.neykov.com.appstudassigment.map;

import android.support.annotation.NonNull;

import java.util.List;

import appstud.neykov.com.appstudassigment.base.ErrorDisplayView;
import appstud.neykov.com.appstudassigment.networking.places.Location;
import appstud.neykov.com.appstudassigment.networking.places.Place;

public interface MapView extends ErrorDisplayView{

    int ERROR_LOADING_PLACES = 1000;
    int ERROR_LOCATION_UPDATES_UNAVAILABLE = 1002;

    public void displayLocation(@NonNull Location location);

    public void displayNearbyPlaces(@NonNull Location location, @NonNull List<Place> places);
}
