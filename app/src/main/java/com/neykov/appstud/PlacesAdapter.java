package com.neykov.appstud;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import com.neykov.appstud.base.adapter.MutableRecyclerAdapter;
import com.neykov.appstud.model.Place;
import com.neykov.appstud.model.PlacesImagingContract;

public class PlacesAdapter extends MutableRecyclerAdapter<Place, PlacesAdapter.ViewHolder> {

    private Picasso picasso;

    @Inject
    public PlacesAdapter(Picasso picasso) {
        this.picasso = picasso;
    }

    @Override
    public PlacesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_place, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place place = getItem(position);
        if(!place.photos().isEmpty()) {
            picasso.load(PlacesImagingContract.getPhotoUri(place.photos().get(0)))
                    .fit().centerCrop()
                    .into(holder.placePhotoView);
        } else {
            picasso.cancelRequest(holder.placePhotoView);
            holder.placePhotoView.setImageDrawable(null);
        }

        holder.placeNameView.setText(place.name());
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView placePhotoView;
        private TextView placeNameView;

        public ViewHolder(View itemView) {
            super(itemView);
            placePhotoView = (ImageView) itemView.findViewById(R.id.photo);
            placeNameView = (TextView) itemView.findViewById(R.id.name);
        }
    }
}

