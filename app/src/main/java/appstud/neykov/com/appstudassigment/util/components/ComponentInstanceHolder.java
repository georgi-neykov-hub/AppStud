package appstud.neykov.com.appstudassigment.util.components;

import android.support.annotation.NonNull;

public abstract class ComponentInstanceHolder<T> implements ComponentHolder<T> {

    private final Object componentLock = new Object();
    private T instance;

    @NonNull
    @Override
    public T component() {
        synchronized (componentLock){
            if(instance == null){
                instance = createComponent();
            }

            return instance;
        }
    }

    protected abstract T createComponent();

    public void clear(){
        synchronized (componentLock){
            instance = null;
        }
    }
}
