package appstud.neykov.com.appstudassigment.base.fragments;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.neykov.mvp.Presenter;
import com.neykov.mvp.PresenterFactory;
import com.neykov.mvp.SupportPresenterLifecycleDelegate;
import com.neykov.mvp.ViewWithPresenter;

import appstud.neykov.com.appstudassigment.base.ErrorDisplayDelegate;
import appstud.neykov.com.appstudassigment.base.ErrorDisplayView;

public abstract class ViewFragment<P extends Presenter> extends Fragment
        implements ViewWithPresenter<P>, PresenterFactory<P>, ErrorDisplayView {

    private final SupportPresenterLifecycleDelegate<P> presenterDelegate = new SupportPresenterLifecycleDelegate<>(this);
    private ErrorDisplayDelegate errorDisplayDelegate;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenterDelegate.onCreate(savedInstanceState, getActivity().getSupportFragmentManager());
        errorDisplayDelegate = new ErrorDisplayDelegate(getContext());
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        errorDisplayDelegate.attachView(view);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        presenterDelegate.onSaveInstanceState(bundle
        );
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        errorDisplayDelegate.detachView();
        super.onDestroyView();
    }

    @CallSuper
    @Override
    public void onDestroy() {
        super.onDestroy();
        errorDisplayDelegate = null;
        presenterDelegate.onDestroy(getActivity().isFinishing());
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        presenterDelegate.onResume(this);
    }

    @CallSuper
    @Override
    public void onPause() {
        presenterDelegate.onPause(getActivity().isFinishing());
        super.onPause();
    }

    @Override
    public final P getPresenter() {
        return presenterDelegate.getPresenter();
    }

    @Override
    public void showError(int errorType, @NonNull Bundle data) {
        errorDisplayDelegate.showError(errorType, data);
    }
}