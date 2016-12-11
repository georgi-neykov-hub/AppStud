package appstud.neykov.com.appstudassigment.map;

public interface LocationPermissionListener {
    void onLocationPermissionGranted();

    void onLocationPermissionDenied(boolean showRationale);
}
