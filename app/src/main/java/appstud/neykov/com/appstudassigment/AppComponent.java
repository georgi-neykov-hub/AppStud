package appstud.neykov.com.appstudassigment;

import javax.inject.Singleton;

import appstud.neykov.com.appstudassigment.networking.NetworkingModule;
import appstud.neykov.com.appstudassigment.util.ApplicationModule;
import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, NetworkingModule.class})
public interface AppComponent {
}
