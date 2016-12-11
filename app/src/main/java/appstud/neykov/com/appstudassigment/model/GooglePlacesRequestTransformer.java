package appstud.neykov.com.appstudassigment.model;

import android.net.Uri;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import static appstud.neykov.com.appstudassigment.model.PlacesImagingContract.getMaxHeight;
import static appstud.neykov.com.appstudassigment.model.PlacesImagingContract.getMaxWidth;
import static appstud.neykov.com.appstudassigment.model.PlacesImagingContract.getReference;

public class GooglePlacesRequestTransformer implements Picasso.RequestTransformer {

    private static final Uri BASE_IMAGE_URI = Uri.parse("https://maps.googleapis.com/maps/api/place/photo");

    private String googleApiToken;

    public GooglePlacesRequestTransformer(String googleApiToken) {
        this.googleApiToken = googleApiToken;
    }

    @Override
    public Request transformRequest(Request request) {

        if (request.uri != null && PlacesImagingContract.isPlacesPhotoUri(request.uri)) {
            Uri photoUri = request.uri;
            String photoReference = getReference(photoUri);
            int maxHeight = getMaxHeight(photoUri);
            int maxWidth = getMaxWidth(photoUri);

            int clampedHeight = Math.min(maxHeight, request.targetHeight);
            int clampedWidth = Math.min(maxWidth, request.targetWidth);

            Uri httpUri = BASE_IMAGE_URI.buildUpon()
                    .appendQueryParameter("key", googleApiToken)
                    .appendQueryParameter("photoreference", photoReference)
                    .appendQueryParameter("maxheight", String.valueOf(clampedHeight))
                    .appendQueryParameter("maxwidth", String.valueOf(clampedWidth))
                    .build();
            request = request.buildUpon().setUri(httpUri).build();
        }

        return request;
    }
}
