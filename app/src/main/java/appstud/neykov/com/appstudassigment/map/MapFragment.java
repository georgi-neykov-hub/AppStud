package appstud.neykov.com.appstudassigment.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.neykov.mvp.SupportPresenterLifecycleDelegate;
import com.neykov.mvp.ViewWithPresenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import appstud.neykov.com.appstudassigment.AppComponent;
import appstud.neykov.com.appstudassigment.R;
import appstud.neykov.com.appstudassigment.networking.places.Location;
import appstud.neykov.com.appstudassigment.networking.places.Place;
import appstud.neykov.com.appstudassigment.util.components.ComponentDelegate;

public class MapFragment extends Fragment implements ViewWithPresenter<MapPresenter>, MapView {

    private static final String TAG_MAPS_FRAGMENT = "MapFragment.TAG_MAPS_FRAGMENT";
    private static final int PERMISSION_REQUEST_CODE = 933;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Inject
    Provider<MapPresenter> presenterProvider;
    private SupportPresenterLifecycleDelegate<MapPresenter> presenterLifecycleDelegate;
    private SupportMapFragment googleMapsFragment;
    private GoogleMap googleMap;
    private LocationSourceAdapter locationSourceAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenterLifecycleDelegate = new SupportPresenterLifecycleDelegate<>(() -> presenterProvider.get());
        presenterLifecycleDelegate.onCreate(savedInstanceState, getFragmentManager());
        googleMapsFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag(TAG_MAPS_FRAGMENT);
        if (googleMapsFragment == null) {
            googleMapsFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .add(R.id.mapContainer, googleMapsFragment, TAG_MAPS_FRAGMENT)
                    .commit();
        }

        new ComponentDelegate<>(getContext(), AppComponent.class)
                .component()
                .createMapComponent()
                .inject(this);

        locationSourceAdapter = new LocationSourceAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleMap != null) {
            presenterLifecycleDelegate.onResume(this);
        }
        if (locationPermissionGranted()) {
            getPresenter().startTrackingLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        presenterLifecycleDelegate.onPause(false);
        getPresenter().stopTrackingLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        googleMapsFragment = null;
        presenterLifecycleDelegate.onDestroy(getActivity().isFinishing() ||
                !getActivity().isChangingConfigurations());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        googleMapsFragment.getMapAsync(this::onGoogleMapAvailable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        googleMap = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (locationPermissionGranted()) {
            onLocationPermissionGranted();
        } else {
            boolean rationaleNeeded = ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION);
            onLocationPermissionDenied(rationaleNeeded);
            if (!rationaleNeeded) {
                requestReadContactsPermission();
            }
        }
    }

    private boolean locationPermissionGranted() {
        return ContextCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onLocationPermissionGranted();
            } else {
                onLocationPermissionDenied(true);
            }
        }
    }

    @Override
    public void displayLocation(@NonNull Location location) {
        LatLng latLng = new LatLng(location.latitude(), location.longtidude());
        CameraUpdate positionUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f);
        googleMap.animateCamera(positionUpdate);
        locationSourceAdapter.notifyLocationUpdated(latLng);
        getPresenter().loadNearbyBars(location);
    }

    @Override
    public void displayNearbyPlaces(@NonNull Location location, @NonNull List<Place> places) {
        googleMap.clear();
        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        for (Place place : places) {
            Location placeLocation = place.location();
            LatLng latLng = new LatLng(placeLocation.latitude(), placeLocation.longtidude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .draggable(false)
                    .position(latLng)
                    .title(place.name());
            googleMap.addMarker(markerOptions);
            boundsBuilder.include(latLng);
        }

        CameraUpdate boundsUpdate = CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 64);
        googleMap.stopAnimation();
        googleMap.animateCamera(boundsUpdate);
    }

    @Override
    public void showError(int errorType, @NonNull Bundle data) {

    }

    @Override
    public MapPresenter getPresenter() {
        return presenterLifecycleDelegate.getPresenter();
    }

    protected final void requestReadContactsPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    protected void onGoogleMapAvailable(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setLocationSource(locationSourceAdapter);
        presenterLifecycleDelegate.onResume(this);
        if (locationPermissionGranted()) {
            //noinspection MissingPermission
            this.googleMap.setMyLocationEnabled(true);
        }
    }

    protected void onLocationPermissionGranted() {
        if (googleMap != null) {
            //noinspection MissingPermission
            googleMap.setMyLocationEnabled(true);
        }

        getPresenter().startTrackingLocation();
    }

    protected void onLocationPermissionDenied(boolean showRationale) {
        if (googleMap != null) {
            //noinspection MissingPermission
            googleMap.setMyLocationEnabled(false);
        }

        getPresenter().stopTrackingLocation();
        //TODO: Add a message/rationale why permission is needed.
    }

    private static class LocationSourceAdapter implements LocationSource {

        private OnLocationChangedListener listener;

        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {
            listener = onLocationChangedListener;
        }

        @Override
        public void deactivate() {
            listener = null;
        }

        public void notifyLocationUpdated(LatLng location) {
            if (listener != null) {
                android.location.Location adaptedLocation = new android.location.Location("");
                adaptedLocation.setLatitude(location.latitude);
                adaptedLocation.setLongitude(location.longitude);
                listener.onLocationChanged(adaptedLocation);
            }
        }
    }
}
