package com.neykov.appstud.util.components;

import android.support.annotation.NonNull;

public interface ComponentHolder<T> {
    @NonNull
    T component();
}
