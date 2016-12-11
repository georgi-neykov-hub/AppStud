package com.neykov.appstud;

import dagger.Subcomponent;

/**
 * Created by Georgi on 12/10/2016.
 */

@Subcomponent
public interface MapComponent {
    void inject(PlacesMapFragment fragment);
    void inject(PlacesListFragment fragment);
    void inject(PlacesFragment fragment);
}
