package com.neykov.appstud;

public interface LocationPermissionListener {
    void onLocationPermissionGranted();

    void onLocationPermissionDenied(boolean showRationale);
}
