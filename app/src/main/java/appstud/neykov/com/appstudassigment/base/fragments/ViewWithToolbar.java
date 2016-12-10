package appstud.neykov.com.appstudassigment.base.fragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

public interface ViewWithToolbar {
    @SuppressWarnings("UnusedParameters")
    @Nullable
    Toolbar onCreateToolbar(View view);

    @SuppressWarnings("UnusedParameters")
    void onConfigureToolbar(@NonNull Toolbar toolbar);

    void setHomeAsUpEnabled(boolean enabled);
}
