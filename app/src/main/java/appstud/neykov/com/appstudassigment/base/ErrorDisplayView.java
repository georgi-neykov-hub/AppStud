package appstud.neykov.com.appstudassigment.base;

import android.os.Bundle;
import android.support.annotation.NonNull;

public interface ErrorDisplayView{
    int ERROR_GENERAL = 1;
    int ERROR_NETWORK = 2;

    void showError(int errorType, @NonNull Bundle data);
}