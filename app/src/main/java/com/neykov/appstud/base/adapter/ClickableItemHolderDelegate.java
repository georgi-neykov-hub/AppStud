package com.neykov.appstud.base.adapter;

import android.support.annotation.Nullable;

public class ClickableItemHolderDelegate implements ClickableItemHolder, ItemClickListener {

    private ItemClickListener listener;

    @Override
    public void setOnItemClickListener(@Nullable ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(int position) {
        if(listener != null) {
            listener.onItemClick(position);
        }
    }
}
