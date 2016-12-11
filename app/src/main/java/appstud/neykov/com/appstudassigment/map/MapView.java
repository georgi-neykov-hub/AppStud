package appstud.neykov.com.appstudassigment.map;

import android.support.annotation.NonNull;

import java.util.List;

import appstud.neykov.com.appstudassigment.base.ErrorDisplayView;
import appstud.neykov.com.appstudassigment.model.Location;
import appstud.neykov.com.appstudassigment.model.Place;

public interface MapView extends ErrorDisplayView, PlacesView {

    int ERROR_LOADING_PLACES = 1000;
    int ERROR_LOCATION_UPDATES_UNAVAILABLE = 1002;

    public void displayLocation(@NonNull Location location);

}
