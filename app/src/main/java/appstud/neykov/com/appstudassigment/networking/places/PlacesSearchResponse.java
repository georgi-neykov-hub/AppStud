package appstud.neykov.com.appstudassigment.networking.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * Created by Georgi on 12/10/2016.
 */

public class PlacesSearchResponse {

    @Expose
    @SerializedName("status")
    private Status status;

    @Expose
    @SerializedName("results")
    private List<Place> results;

    public Status getStatus() {
        return status;
    }

    public List<Place> getResults() {
        return Collections.unmodifiableList(results);
    }

}
