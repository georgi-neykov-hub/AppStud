package appstud.neykov.com.appstudassigment.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
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

    private boolean locationPermissionGranted;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenterLifecycleDelegate = new SupportPresenterLifecycleDelegate<>(() -> presenterProvider.get());
        presenterLifecycleDelegate.onCreate(savedInstanceState, getFragmentManager());
        googleMapsFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag(TAG_MAPS_FRAGMENT);
        if(googleMapsFragment == null) {
            googleMapsFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .add(R.id.mapContainer, googleMapsFragment, TAG_MAPS_FRAGMENT)
                    .commit();
        }

        new ComponentDelegate<>(getContext(), AppComponent.class)
                .component()
                .createMapComponent()
                .inject(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(googleMap != null) {
            presenterLifecycleDelegate.onResume(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        presenterLifecycleDelegate.onPause(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        if (ContextCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
    public void displayPlaces(@NonNull List<Place> places) {
        googleMap.clear();
        for (Place place : places) {
            Location location = place.location();
            LatLng latLng = new LatLng(location.latitude(), location.longtidude());
            CircleOptions options = new CircleOptions();
            options.clickable(false).center(latLng)
                    .strokeColor(Color.BLACK)
                    .radius(5f)
                    .strokeWidth(1f);
            googleMap.addCircle(options);
        }
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
        presenterLifecycleDelegate.onResume(this);
    }

    protected void onLocationPermissionGranted(){
        locationPermissionGranted = true;
        if (googleMap != null) {
            //noinspection MissingPermission
            googleMap.setMyLocationEnabled(true);
        }

        // TODO: Request the current fine/coarse location and use Places API.
    }

    protected void onLocationPermissionDenied(boolean showRationale){
        locationPermissionGranted = false;
        if (googleMap != null) {
            //noinspection MissingPermission
            googleMap.setMyLocationEnabled(false);
        }

        //TODO: Add a message/rationale why permission is needed.
    }
}
