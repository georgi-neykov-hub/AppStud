package appstud.neykov.com.appstudassigment.util.components;

import android.support.annotation.NonNull;

public interface ComponentProvider {
    @NonNull
    <T> T provide(Class<T> componentType);
}
