package appstud.neykov.com.appstudassigment.map;

import android.support.annotation.NonNull;

import java.util.List;

import appstud.neykov.com.appstudassigment.model.Location;
import appstud.neykov.com.appstudassigment.model.Place;

public interface PlacesView {
    void displayNearbyPlaces(@NonNull Location location, @NonNull List<Place> places);
}
