package appstud.neykov.com.appstudassigment.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Georgi on 12/10/2016.
 */

public class PlacePhoto {

    @Expose
    @SerializedName("width")
    private int originalWidth;

    @Expose
    @SerializedName("height")
    private int originalHeight;

    @Expose
    @SerializedName("photo_reference")
    private String reference;

    public int originalWidth() {
        return originalWidth;
    }

    public int originalHeight() {
        return originalHeight;
    }

    public String reference() {
        return reference;
    }
}
