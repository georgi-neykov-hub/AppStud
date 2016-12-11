package appstud.neykov.com.appstudassigment.map;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import appstud.neykov.com.appstudassigment.AppComponent;
import appstud.neykov.com.appstudassigment.R;
import appstud.neykov.com.appstudassigment.base.fragments.RecyclerViewFragment;
import appstud.neykov.com.appstudassigment.model.Location;
import appstud.neykov.com.appstudassigment.model.Place;
import appstud.neykov.com.appstudassigment.util.components.ComponentDelegate;

public class PlacesListFragment extends RecyclerViewFragment<PlacesAdapter, PlacesViewPresenter> implements PlacesView, LocationPermissionListener {

    private static final int PERMISSION_REQUEST_CODE = 123;

    public static PlacesListFragment newInstance() {
        return new PlacesListFragment();
    }

    @Inject
    PlacesAdapter adapter;
    @Inject
    Provider<PlacesViewPresenter> presenterProvider;
    private LocationPermissionDelegate permissionDelegate;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        permissionDelegate = new LocationPermissionDelegate(this, this, PERMISSION_REQUEST_CODE);
        new ComponentDelegate<>(getContext(), AppComponent.class)
                .component()
                .createMapComponent()
                .inject(this);
        setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_places_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        permissionDelegate.checkPermissionState();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void displayLocation(@NonNull Location location) {
        getPresenter().loadNearbyBars(location);
    }

    @Override
    public void displayNearbyPlaces(@NonNull Location location, @NonNull List<Place> places) {
        getAdapter().setItems(places);
    }

    @NonNull
    @Override
    protected RecyclerView onConfigureItemView(@NonNull View rootView, @Nullable Bundle savedState) {
        return (RecyclerView) rootView.findViewById(R.id.list);
    }

    @Override
    public void onLocationPermissionGranted() {
        getPresenter().startTrackingLocation();
    }

    @Override
    public void onLocationPermissionDenied(boolean showRationale) {
        getPresenter().stopTrackingLocation();
    }

    @Override
    public PlacesViewPresenter createPresenter() {
        return presenterProvider.get();
    }
}
