package appstud.neykov.com.appstudassigment.networking;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import appstud.neykov.com.appstudassigment.BuildConfig;
import appstud.neykov.com.appstudassigment.R;
import appstud.neykov.com.appstudassigment.util.Global;
import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * @author Georgi
 *         A Dagger 2 module providing network-related dependencies
 */
@Module
public class NetworkingModule {

    @Provides
    @Singleton
    public GoogleApiClient provideApiClient(@Global Context context){
        return new GoogleApiClient.Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Provides
    @Singleton
    Picasso providePicasso(@Global Context context, OkHttpClient httpClient) {
        return new Picasso.Builder(context)
                .loggingEnabled(BuildConfig.DEBUG)
                .downloader(new OkHttp3Downloader(httpClient))
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
}
