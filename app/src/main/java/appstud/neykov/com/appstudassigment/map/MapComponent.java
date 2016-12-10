package appstud.neykov.com.appstudassigment.map;

import dagger.Subcomponent;

/**
 * Created by Georgi on 12/10/2016.
 */

@Subcomponent
public interface MapComponent {
    void inject(MapFragment fragment);
}
