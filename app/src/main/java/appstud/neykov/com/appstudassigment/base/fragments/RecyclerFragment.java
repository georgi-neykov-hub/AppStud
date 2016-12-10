package appstud.neykov.com.appstudassigment.base.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class RecyclerFragment<A extends RecyclerView.Adapter<? extends RecyclerView.ViewHolder>> extends Fragment {

    private static final String KEY_LAYOUT_MANAGER_STATE = "ListFragment.KEY_LAYOUT_MANAGER_STATE";

    private RecyclerView recyclerView;
    private View emptyStateView;
    private A adapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = onConfigureItemView(view, savedInstanceState);
        //noinspection ConstantConditions
        if(recyclerView == null){
            throw new IllegalStateException("Null view returned from onConfigureItemView().");
        }
        RecyclerView.LayoutManager layoutManager = onCreateLayoutManager(savedInstanceState);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(getAdapter());
        emptyStateView = onConfigureEmptyStateView(view, savedInstanceState);
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        if (recyclerView != null) {
            recyclerView.setAdapter(null);
            recyclerView = null;
        }
        emptyStateView = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter = null;
    }

    @Override
    @CallSuper
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        final RecyclerView.LayoutManager layoutManager = getRecyclerView() != null ?
                getRecyclerView().getLayoutManager() : null;
        if (layoutManager != null) {
            outState.putParcelable(KEY_LAYOUT_MANAGER_STATE, getRecyclerView().getLayoutManager().onSaveInstanceState());
        }
    }

    @Nullable
    protected View onConfigureEmptyStateView(@NonNull View rootView, @Nullable Bundle savedState) {
        return null;
    }

    @NonNull
    protected abstract RecyclerView onConfigureItemView(@NonNull View rootView, @Nullable Bundle savedState);

    @NonNull
    protected RecyclerView.LayoutManager onCreateLayoutManager(@Nullable Bundle savedState) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);;
        if (savedState != null) {
            Parcelable layoutState = savedState.getParcelable(KEY_LAYOUT_MANAGER_STATE);
            if (layoutState != null) {
                layoutManager.onRestoreInstanceState(layoutState);
            }
        }
        return layoutManager;
    }

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Nullable
    protected View getEmptyStateView(){
        return emptyStateView;
    }

    protected void setAdapter(A adapter) {
        this.adapter = adapter;
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }
    }

    public final void setEmptyState(boolean isEmpty) {
        if (emptyStateView != null) {
            emptyStateView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
    }

    protected A getAdapter() {
        return adapter;
    }
}
