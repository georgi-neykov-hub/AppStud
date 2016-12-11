package com.neykov.appstud.networking;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.neykov.appstud.BuildConfig;
import com.neykov.appstud.R;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import com.neykov.appstud.model.GooglePlacesRequestTransformer;
import com.neykov.appstud.model.Place;
import com.neykov.appstud.networking.places.GoogleApisKey;
import com.neykov.appstud.networking.places.GooglePlacesApi;
import com.neykov.appstud.util.Global;
import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Georgi
 *  A Dagger 2 module providing network-related dependencies
 */
@Module
public class NetworkingModule {

    @Provides
    @GoogleApisKey
    public String provideGoogleApisToken(@Global Context context){
        return context.getString(R.string.config_google_apis_key);
    }

    @Provides
    @Singleton
    GoogleApiClient provideGoogleApiClient(@Global Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build();
    }

    @Provides
    @Singleton
    GooglePlacesApi provideAPI(OkHttpClient okHttpClient, Gson gson) {
        final String endpointUrl = "https://maps.googleapis.com/maps/api/place/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endpointUrl)
                .client(okHttpClient)
                .validateEagerly(BuildConfig.DEBUG)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(GooglePlacesApi.class);
    }

    @Provides
    @Singleton
    Picasso providePicasso(@Global Context context, OkHttpClient httpClient, @GoogleApisKey String googlePlacesKey) {
        return new Picasso.Builder(context)
                .loggingEnabled(BuildConfig.DEBUG)
                .downloader(new OkHttp3Downloader(httpClient))
                .requestTransformer(new GooglePlacesRequestTransformer(googlePlacesKey))
                .build();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(@Global Context context) {
        final Resources resources = context.getResources();
        final int cacheSizeBytes = resources.getInteger(R.integer.config_httpCacheSizeMb) * 1024 * 1024;
        final int connectTimeoutMs = resources.getInteger(R.integer.config_httpConnectTimeoutMs);
        final int readTimeoutMs = resources.getInteger(R.integer.config_httpReadTimeoutMs);
        final int writeTimeoutMs = resources.getInteger(R.integer.config_httpWriteTimeoutMs);

        return new OkHttpClient.Builder()
                .cache(new Cache(context.getCacheDir(), cacheSizeBytes))
                .connectTimeout(connectTimeoutMs, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeoutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeoutMs, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Place.class, new Place.Deserializer())
                // Add other custom serializers/deserializers here
                .create();
    }
}
