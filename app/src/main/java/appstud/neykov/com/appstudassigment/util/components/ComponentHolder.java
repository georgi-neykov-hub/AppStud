package appstud.neykov.com.appstudassigment.util.components;

import android.support.annotation.NonNull;

public interface ComponentHolder<T> {
    @NonNull
    T component();
}
