package appstud.neykov.com.appstudassigment.map;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import appstud.neykov.com.appstudassigment.AppComponent;
import appstud.neykov.com.appstudassigment.R;
import appstud.neykov.com.appstudassigment.base.fragments.RecyclerFragment;
import appstud.neykov.com.appstudassigment.model.Location;
import appstud.neykov.com.appstudassigment.model.Place;
import appstud.neykov.com.appstudassigment.util.components.ComponentDelegate;

public class PlacesListFragment extends RecyclerFragment<PlacesAdapter> implements PlacesView{

    public static PlacesListFragment newInstance() {
        return new PlacesListFragment();
    }

    @Inject
    PlacesAdapter adapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
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
    public void displayNearbyPlaces(@NonNull Location location, @NonNull List<Place> places) {
        getAdapter().setItems(places);
    }

    @NonNull
    @Override
    protected RecyclerView onConfigureItemView(@NonNull View rootView, @Nullable Bundle savedState) {
        return (RecyclerView) rootView.findViewById(R.id.list);
    }
}
