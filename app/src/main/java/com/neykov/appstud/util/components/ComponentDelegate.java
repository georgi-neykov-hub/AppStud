package com.neykov.appstud.util.components;

import android.content.Context;
import android.support.annotation.NonNull;

public class ComponentDelegate<T> implements ComponentHolder<T> {

    private T component;

    public ComponentDelegate(Context context, Class<T> componentType) {
        this.component = ((ComponentProvider)context.getApplicationContext()).provide(componentType);
    }

    @NonNull
    @Override
    public T component() {
        return component;
    }
}
