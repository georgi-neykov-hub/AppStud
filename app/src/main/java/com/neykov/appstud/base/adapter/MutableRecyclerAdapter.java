package com.neykov.appstud.base.adapter;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class MutableRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> mItems;

    protected MutableRecyclerAdapter() {
        this(new ArrayList<>());
    }

    protected MutableRecyclerAdapter(List<T> dataSet) {
        if(dataSet == null){
            throw new IllegalArgumentException("Null dataset argument provided.");
        }
        this.mItems = dataSet;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public T getItem(int position) {
        return mItems.get(position);
    }

    @CallSuper
    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    @CallSuper
    public void deleteItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @CallSuper
    public boolean deleteItem(T item) {
        int position = mItems.indexOf(item);
        if (position != -1) {
            deleteItem(position);
            return true;
        }

        return false;
    }

    @CallSuper
    public void addItems(Collection<T> newItems) {
        if (newItems == null) {
            throw new IllegalArgumentException("Null collection provided.");
        } else if (!newItems.isEmpty()) {
            int startPosition = mItems.size();
            mItems.addAll(newItems);
            notifyItemChanged(startPosition, newItems.size());
        }
    }

    @CallSuper
    public void clearItems() {
        final int count = getItemCount();
        if (count > 0) {
            mItems.clear();
            notifyItemRangeRemoved(0, count);
        }
    }

    @CallSuper
    public void swapItem(int position, T replacement) {
        mItems.set(position, replacement);
        notifyItemChanged(position, null); // Keep the null argument to force a bind of the item
    }

    @CallSuper
    public void setItems(Collection<T> items) {
        if (this.mItems != items) {
            this.mItems = new ArrayList<>(items);
            notifyDataSetChanged();
        }
    }

    public final List<T> getItems() {
        return mItems;
    }
}