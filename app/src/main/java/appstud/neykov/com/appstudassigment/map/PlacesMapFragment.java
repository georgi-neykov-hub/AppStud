package appstud.neykov.com.appstudassigment.map;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.neykov.mvp.SupportPresenterLifecycleDelegate;
import com.neykov.mvp.ViewWithPresenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import appstud.neykov.com.appstudassigment.AppComponent;
import appstud.neykov.com.appstudassigment.R;
import appstud.neykov.com.appstudassigment.model.Location;
import appstud.neykov.com.appstudassigment.model.Place;
import appstud.neykov.com.appstudassigment.util.components.ComponentDelegate;

public class PlacesMapFragment extends Fragment implements PlacesView, ViewWithPresenter<PlacesViewPresenter>, LocationPermissionListener {

    private static final String TAG_MAPS_FRAGMENT = "MapFragment.TAG_MAPS_FRAGMENT";

    private static final int PERMISSION_REQUEST_CODE = 933;

    public static PlacesMapFragment newInstance() {
        return new PlacesMapFragment();
    }

    @Inject
    MarkerImageLoader markerImageLoader;
    @Inject
    Provider<PlacesViewPresenter> presenterProvider;

    private LocationPermissionDelegate permissionDelegate;
    private SupportPresenterLifecycleDelegate<PlacesViewPresenter> presenterLifecycleDelegate;

    private SupportMapFragment googleMapsFragment;
    private GoogleMap googleMap;
    private LocationSourceAdapter locationSourceAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionDelegate = new LocationPermissionDelegate(this, this, PERMISSION_REQUEST_CODE);
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

        int strokeColor = ContextCompat.getColor(getContext(), R.color.marker_image_stroke_color);
        int markerStrokeWidth = getContext().getResources().getDimensionPixelSize(R.dimen.map_place_image_stroke);
        markerImageLoader.setMarkerBorderColor(strokeColor);
        markerImageLoader.setMarkerBorderWidthPixels(markerStrokeWidth);
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
    public void onStart() {
        super.onStart();
        permissionDelegate.checkPermissionState();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenterLifecycleDelegate.onPause(false);
        getPresenter().stopTrackingLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        markerImageLoader.cancelImageLoadRequests();
        googleMap = null;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenterLifecycleDelegate.onDestroy(getActivity().isFinishing() || !getActivity().isChangingConfigurations());
        googleMapsFragment = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void displayLocation(@NonNull Location location) {
        if (googleMap != null) {
            displayLocation(googleMap, location);
        }
        getPresenter().loadNearbyBars(location);
    }

    private void displayLocation(@NonNull GoogleMap googleMap, @NonNull Location location) {
        LatLng latLng = new LatLng(location.latitude(), location.longtidude());
        CameraUpdate positionUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f);
        googleMap.animateCamera(positionUpdate);
        locationSourceAdapter.notifyLocationUpdated(latLng);
    }

    @Override
    public void displayNearbyPlaces(@NonNull Location location, @NonNull List<Place> places) {
        if (googleMap != null) {
            displayPlaces(googleMap, places);
        }
    }

    private void displayPlaces(@NonNull GoogleMap googleMap, @NonNull List<Place> places) {
        markerImageLoader.cancelImageLoadRequests();
        googleMap.clear();
        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        for (Place place : places) {
            Location placeLocation = place.location();
            LatLng latLng = new LatLng(placeLocation.latitude(), placeLocation.longtidude());
            if (!place.photos().isEmpty()) {
                markerImageLoader.loadPlaceImage(googleMap, place.photos().get(0), latLng);
            } else {
                markerImageLoader.displayPlaceWithoutImage(googleMap, latLng);
            }
            boundsBuilder.include(latLng);
        }

        int paddingPixes = getContext().getResources().getDimensionPixelSize(R.dimen.map_place_image_diameter);
        CameraUpdate boundsUpdate = CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), paddingPixes);
        googleMap.stopAnimation();
        googleMap.animateCamera(boundsUpdate);
    }

    protected void onGoogleMapAvailable(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setLocationSource(locationSourceAdapter);
        if (permissionDelegate.locationPermissionGranted()) {
            //noinspection MissingPermission
            this.googleMap.setMyLocationEnabled(true);
        }

        presenterLifecycleDelegate.onResume(this);
    }

    @Override
    public void onLocationPermissionGranted() {
        if (googleMap != null) {
            //noinspection MissingPermission
            googleMap.setMyLocationEnabled(true);
        }
        getPresenter().startTrackingLocation();
    }

    @Override
    public void onLocationPermissionDenied(boolean showRationale) {
        if (googleMap != null) {
            //noinspection MissingPermission
            googleMap.setMyLocationEnabled(false);
        }
        getPresenter().stopTrackingLocation();
    }

    @Override
    public PlacesViewPresenter getPresenter() {
        return presenterLifecycleDelegate.getPresenter();
    }

    @Override
    public void showError(int errorType, @NonNull Bundle data) {

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

        void notifyLocationUpdated(LatLng location) {
            if (listener != null) {
                android.location.Location adaptedLocation = new android.location.Location("");
                adaptedLocation.setLatitude(location.latitude);
                adaptedLocation.setLongitude(location.longitude);
                listener.onLocationChanged(adaptedLocation);
            }
        }
    }
}
