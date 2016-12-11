package appstud.neykov.com.appstudassigment.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;
import javax.inject.Provider;

import appstud.neykov.com.appstudassigment.AppComponent;
import appstud.neykov.com.appstudassigment.R;
import appstud.neykov.com.appstudassigment.util.components.ComponentDelegate;

public class PlacesFragment extends Fragment {

    public static PlacesFragment newInstance() {
        return new PlacesFragment();
    }

    @Inject
    Provider<PlacesViewPresenter> presenterProvider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ComponentDelegate<>(getContext(), AppComponent.class)
                .component()
                .createMapComponent()
                .inject(this);

        Fragment currentView = getCurrentView();
        if (currentView == null) {
            openMapScreen();
        }
    }

    private Fragment getCurrentView() {
        return getChildFragmentManager().findFragmentById(R.id.content);
    }

    private void openMapScreen() {
        Fragment currentView = getCurrentView();
        if (currentView != null && currentView instanceof PlacesMapFragment) {
            return;
        }
        PlacesMapFragment mapFragment = PlacesMapFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.content, mapFragment)
                .commit();
    }

    private void openListScreen() {
        Fragment currentView = getCurrentView();
        if (currentView != null && currentView instanceof PlacesListFragment) {
            return;
        }
        PlacesListFragment listFragment = PlacesListFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.content, listFragment)
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_places, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        BottomNavigationView navigationView = (BottomNavigationView) view.findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    openMapScreen();
                    break;
                case R.id.navigation_list:
                    openListScreen();
                    break;
                default:
                    return false;
            }

            return true;
        });
    }
}
