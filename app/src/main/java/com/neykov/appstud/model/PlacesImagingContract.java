package com.neykov.appstud.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;

public class PlacesImagingContract {

    private static final String AUTHORITY = "com.neykov.appstud.places";
    private static final String FRAGMENT_PHOTOS = "photos";
    private static final Uri BASE_CONTENT_URI = Uri.EMPTY.buildUpon()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(AUTHORITY)
            .appendPath(FRAGMENT_PHOTOS)
            .build();

    private static final String PARAMETER_MAX_HEIGHT = "maxHeight";
    private static final String PARAMETER_MAX_WIDTH = "maxWidth";

    private PlacesImagingContract(){
    }

    public static boolean isPlacesPhotoUri(@NonNull Uri uri) {
        return AUTHORITY.equals(uri.getAuthority()) &&
                !uri.getPathSegments().isEmpty() &&
                FRAGMENT_PHOTOS.equals(uri.getPathSegments().get(0));
    }

    public static Uri getPhotoUri(@NonNull PlacePhoto photo) {
        return BASE_CONTENT_URI.buildUpon().appendPath(photo.reference())
                .appendQueryParameter(PARAMETER_MAX_HEIGHT, String.valueOf(photo.originalHeight()))
                .appendQueryParameter(PARAMETER_MAX_WIDTH, String.valueOf(photo.originalWidth()))
                .build();
    }

    static String getReference(Uri photoUri){
        return photoUri.getLastPathSegment();
    }

    static int getMaxHeight(Uri photoUri){
        return Integer.parseInt(photoUri.getQueryParameter(PARAMETER_MAX_HEIGHT));
    }

    static int getMaxWidth(Uri photoUri){
        return Integer.parseInt(photoUri.getQueryParameter(PARAMETER_MAX_WIDTH));
    }
}
