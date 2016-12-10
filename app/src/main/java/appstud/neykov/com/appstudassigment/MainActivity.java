package appstud.neykov.com.appstudassigment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import appstud.neykov.com.appstudassigment.map.MapFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, MapFragment.newInstance())
                    .commit();
        }
    }
}
