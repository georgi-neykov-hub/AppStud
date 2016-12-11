package com.neykov.appstud.util;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(@NonNull Application application) {
        this.mApplication = application;
    }

    @Provides
    Application provideApplication(){
        return mApplication;
    }

    @Provides
    @Global
    Context provideApplicationContext(){
        return mApplication.getApplicationContext();
    }
}
