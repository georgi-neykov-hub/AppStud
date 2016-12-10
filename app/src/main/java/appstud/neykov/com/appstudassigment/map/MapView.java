package appstud.neykov.com.appstudassigment.map;

import android.support.annotation.NonNull;

import java.util.List;

import appstud.neykov.com.appstudassigment.base.ErrorDisplayView;
import appstud.neykov.com.appstudassigment.networking.places.Place;

/**
 * Created by Georgi on 12/10/2016.
 */

public interface MapView extends ErrorDisplayView{

    int ERROR_LOADING_PLACES = 1000;

    public void displayPlaces(@NonNull List<Place> places);
}
