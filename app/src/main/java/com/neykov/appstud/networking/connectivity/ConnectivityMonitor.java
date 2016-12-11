package com.neykov.appstud.networking.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.neykov.appstud.util.Global;
import rx.Observable;
import rx.subscriptions.Subscriptions;

/**
 * A simple utility class for monitoring and querying the current network connectivity state.
 */
@Singleton
public class ConnectivityMonitor {

    private Context context;
    private ConnectivityManager connectivityManager;

    @Inject
    public ConnectivityMonitor(@Global Context context) {
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.context = context;
    }

    @NonNull
    public Observable<ConnectivityStatus> getConnectivityStatusStream() {
        return Observable.create(subscriber -> {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (!subscriber.isUnsubscribed()) {
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        ConnectivityStatus connectivityStatus = resolveConnectivityStatus(networkInfo);
                        subscriber.onNext(connectivityStatus);
                    }
                }
            };

            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(receiver, filter);

            subscriber.add(Subscriptions.create(() -> context.unregisterReceiver(receiver)));
        });
    }

    /**
     * Returns a {@linkplain ConnectivityStatus} object resembling the current network connectivity status.
     */
    @NonNull
    public ConnectivityStatus getConnectivityStatus() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return resolveConnectivityStatus(networkInfo);
        } else {
            return ConnectivityStatus.OFFLINE;
        }
    }

    public void checkConnectionAndThrow() throws NoNetworkException {
        if (!isConnectedToNetwork()) {
            throw new NoNetworkException();
        }
    }

    public boolean isConnectedToNetwork() {
        return getConnectivityStatus() != ConnectivityStatus.OFFLINE;
    }

    @NonNull
    private ConnectivityStatus resolveConnectivityStatus(@Nullable NetworkInfo networkInfo) {
        if (networkInfo != null) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return ConnectivityStatus.CONNECTED_WIFI;
                case ConnectivityManager.TYPE_MOBILE:
                    return ConnectivityStatus.CONNECTED_MOBILE;
                default:
                    if (networkInfo.isConnected()) {
                        return ConnectivityStatus.CONNECTED_OTHER;
                    } else {
                        return ConnectivityStatus.OFFLINE;
                    }
            }
        } else {
            return ConnectivityStatus.OFFLINE;
        }
    }
}
