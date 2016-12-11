package com.neykov.appstud;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

public class LocationPermissionDelegate {

    private LocationPermissionListener listener;
    private Fragment fragment;
    private int requestCode;

    public LocationPermissionDelegate(LocationPermissionListener listener, Fragment fragment, int requestCode) {
        this.listener = listener;
        this.fragment = fragment;
        this.requestCode = requestCode;
    }

    public void checkPermissionState() {
        if (locationPermissionGranted()) {
            listener.onLocationPermissionGranted();
        } else {
            boolean rationaleNeeded = ActivityCompat.shouldShowRequestPermissionRationale(
                    fragment.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION);
            listener.onLocationPermissionDenied(rationaleNeeded);
            if (!rationaleNeeded) {
                requestReadContactsPermission();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (this.requestCode == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listener.onLocationPermissionGranted();
            } else {
                listener.onLocationPermissionDenied(true);
            }
        }
    }

    protected final void requestReadContactsPermission() {
        fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
    }

    public boolean locationPermissionGranted() {
        return ContextCompat.checkSelfPermission(
                fragment.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
