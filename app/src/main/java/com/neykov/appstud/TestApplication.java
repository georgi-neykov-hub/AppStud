package com.neykov.appstud;

import android.app.Application;
import android.support.annotation.NonNull;

import com.neykov.appstud.util.ApplicationModule;
import com.neykov.appstud.util.components.ComponentInstanceHolder;
import com.neykov.appstud.util.components.ComponentProvider;

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
