package appstud.neykov.com.appstudassigment;

import android.app.Application;
import android.support.annotation.NonNull;

import appstud.neykov.com.appstudassigment.util.ApplicationModule;
import appstud.neykov.com.appstudassigment.util.components.ComponentInstanceHolder;
import appstud.neykov.com.appstudassigment.util.components.ComponentProvider;

/**
 * Created by Georgi on 12/10/2016.
 */

public class TestApplication extends Application implements ComponentProvider {

    private ComponentInstanceHolder<AppComponent> appComponentHolder = new AppComponentHolder(this);

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T> T provide(Class<T> componentType) {
        if (componentType == AppComponent.class) {
            return (T) appComponentHolder.component();
        } else {
            throw new UnsupportedOperationException("Cannot provide " +
                    componentType.getSimpleName() +
                    ", unknown component type.");
        }
    }

    private class AppComponentHolder extends ComponentInstanceHolder<AppComponent> {

        private Application application;

        AppComponentHolder(Application application) {
            this.application = application;
        }

        @Override
        protected AppComponent createComponent() {
            return DaggerAppComponent.builder()
                    .applicationModule(new ApplicationModule(application))
                    .build();
        }
    }
}
