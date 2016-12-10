package appstud.neykov.com.appstudassigment.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

@SuppressWarnings("ConstantConditions")
public class AttachHelper {

    private AttachHelper() {
        // no instantiation.
    }

    @NonNull
    public static <T> T parentActivityAsListener(Fragment fragment, Class<T> typeOf) {
        return getParentAsListener(fragment.getActivity(), typeOf, true);
    }

    @NonNull
    public static <T> T parentFragmentAsListener(Fragment fragment, Class<T> typeOf) {
        return getParentAsListener(fragment.getParentFragment(), typeOf, true);
    }

    @NonNull
    public static <T> T parentAsListener(Fragment fragment, Class<T> typeOf) {
        if(fragment.getParentFragment() != null){
            return getParentAsListener(fragment.getParentFragment(), typeOf, true);
        } else {
            return getParentAsListener(fragment.getActivity(), typeOf, true);
        }
    }

    @NonNull
    public static <T> T tryParentAsListener(Fragment fragment, Class<T> typeOf) {
        if(fragment.getParentFragment() != null){
            return getParentAsListener(fragment.getParentFragment(), typeOf, false);
        } else {
            return getParentAsListener(fragment.getActivity(), typeOf, false);
        }
    }

    @Nullable
    public static <T> T tryActivityAsListener(Fragment fragment, Class<T> typeOf) {
        return getParentAsListener(fragment.getActivity(), typeOf, false);
    }

    @Nullable
    public static <T> T tryParentFragmentAsListener(Fragment fragment, Class<T> typeOf) {
        return getParentAsListener(fragment.getParentFragment(), typeOf, false);
    }

    private static <T> T getParentAsListener(Object parent, Class<T> typeOf, boolean throwOnMismatch) {
        if (typeOf.isAssignableFrom(parent.getClass())) {
            //noinspection unchecked
            return (T) parent;
        } else if (throwOnMismatch) {
            throw new IllegalStateException(parent.getClass().getCanonicalName()
                    + " must implement " +
                    typeOf.getCanonicalName());
        } else {
            return null;
        }
    }
}