package appstud.neykov.com.appstudassigment.networking.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * Created by Georgi on 12/10/2016.
 */

public class Location {

    @Expose
    @SerializedName("lat")
    private double latitude;

    @Expose
    @SerializedName("lng")
    private double longtidude;

    public Location(double latitude, double longtidude) {
        this.latitude = latitude;
        this.longtidude = longtidude;
    }

    public double latitude() {
        return latitude;
    }

    public double longtidude() {
        return longtidude;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%.6f,%.6f", latitude, longtidude);
    }
}
