package com.neykov.appstud;

import javax.inject.Singleton;

import com.neykov.appstud.networking.NetworkingModule;
import com.neykov.appstud.util.ApplicationModule;
import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, NetworkingModule.class})
public interface AppComponent {

    MapComponent createMapComponent();
}
