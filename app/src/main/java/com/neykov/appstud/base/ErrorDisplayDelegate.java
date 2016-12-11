package com.neykov.appstud.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.neykov.appstud.R;
import com.neykov.appstud.util.LogUtils;


public class ErrorDisplayDelegate implements ErrorDisplayView {
    private static final String TAG = ErrorDisplayDelegate.class.getSimpleName();
    private Context context;
    private View rootView;

    public ErrorDisplayDelegate(Context context) {
        this.context = context;
    }

    public void attachView(@NonNull View rootView) {
        this.rootView = rootView;
    }

    public void detachView() {
        rootView = null;
    }

    @Override
    public void showError(int errorType, @NonNull Bundle data) {
        if (rootView == null) {
            LogUtils.warn(TAG, "showError() called, without an attached view.");
            return;
        }

        switch (errorType) {
            case ERROR_GENERAL:
                //noinspection ConstantConditions
                Snackbar.make(rootView, R.string.error_general, Snackbar.LENGTH_LONG)
                        .show();
                break;

            case ERROR_NETWORK:
                Snackbar.make(rootView, R.string.error_no_network, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.label_settings, v -> {
                            context.startActivity(new Intent(Settings.ACTION_SETTINGS)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        })
                        .show();
        }
    }
}
