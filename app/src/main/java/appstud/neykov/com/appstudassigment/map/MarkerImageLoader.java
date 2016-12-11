package appstud.neykov.com.appstudassigment.map;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import appstud.neykov.com.appstudassigment.R;
import appstud.neykov.com.appstudassigment.model.PlacePhoto;
import appstud.neykov.com.appstudassigment.model.PlacesImagingContract;

public class MarkerImageLoader {

    private Picasso picasso;
    private final Object markerImageTag = new Object();

    private List<Target> imageLoadTargets;
    private RoundedTransformationBuilder transformationBuilder;

    @Inject
    public MarkerImageLoader(@NonNull Picasso picasso) {
        this.picasso = picasso;
        this.imageLoadTargets = new LinkedList<>();
        transformationBuilder = new RoundedTransformationBuilder()
                .scaleType(ImageView.ScaleType.CENTER_CROP)
                .oval(true);
    }

    public void setMarkerBorderColor(@ColorInt int markerBorderColor) {
        transformationBuilder.borderColor(markerBorderColor);
    }

    public void setMarkerBorderWidthPixels(@Dimension int markerBorderWidthPixels) {
        transformationBuilder.borderWidth(markerBorderWidthPixels);
    }

    public void cancelImageLoadRequests(){
        picasso.cancelTag(markerImageTag);
        imageLoadTargets.clear();
    }

    public void displayPlaceWithoutImage(GoogleMap map, LatLng position) {
        picasso.load(R.drawable.ic_bottle)
                .tag(markerImageTag)
                .resizeDimen(R.dimen.map_place_image_diameter, R.dimen.map_place_image_diameter)
                .centerCrop()
                .error(R.drawable.marker)
                .transform(transformationBuilder.build())
                .into(new MarkerLoadTarget(map, position));
    }

    private void displayPlaceWithImage(GoogleMap map, Bitmap bitmap, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions()
                .draggable(false)
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        map.addMarker(markerOptions);
    }

    public void loadPlaceImage(GoogleMap map, PlacePhoto photo, LatLng position) {
        picasso.load(PlacesImagingContract.getPhotoUri(photo))
                .tag(markerImageTag)
                .resizeDimen(R.dimen.map_place_image_diameter, R.dimen.map_place_image_diameter)
                .centerCrop()
                .transform(transformationBuilder.build())
                .into(new MarkerLoadTarget(map, position));
    }

    private class MarkerLoadTarget implements Target {
        private final GoogleMap map;
        private final LatLng position;

        public MarkerLoadTarget(GoogleMap map, LatLng position) {
            this.map = map;
            this.position = position;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            displayPlaceWithImage(map, bitmap, position);
            imageLoadTargets.remove(this);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            displayPlaceWithoutImage(map, position);
            imageLoadTargets.remove(this);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            imageLoadTargets.add(this);
        }
    }
}
