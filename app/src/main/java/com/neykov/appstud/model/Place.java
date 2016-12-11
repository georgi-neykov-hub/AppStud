package com.neykov.appstud.model;

import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Georgi on 12/10/2016.
 */

public class Place {

    @Expose
    @SerializedName("place_id")
    private String id;

    @Expose
    @SerializedName("name")
    private String name;


    @Expose
    @SerializedName("photos")
    private List<PlacePhoto> photos;

    private Location location;

    public Place(String id, String name, List<PlacePhoto> photos, Location location) {
        this.id = id;
        this.name = name;
        this.photos = Collections.unmodifiableList(photos);
        this.location = location;
    }

    @NonNull
    public String id() {
        return id;
    }

    @NonNull
    public String name() {
        return name;
    }

    @NonNull
    public List<PlacePhoto> photos() {
        return photos;
    }

    @NonNull
    public Location location() {
        return location;
    }

    public static class Deserializer implements JsonDeserializer<Place> {

        @Override
        public Place deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            try {
                Location location = context.deserialize(jsonObject.getAsJsonObject("geometry").get("location"), Location.class);
                List<PlacePhoto> photos;
                JsonArray photosJson = jsonObject.getAsJsonArray("photos");
                if (photosJson != null && photosJson.size() > 0) {
                    photos = new ArrayList<>(photosJson.size());
                    for (JsonElement element : photosJson) {
                        photos.add(context.deserialize(element, PlacePhoto.class));
                    }
                } else {
                    photos = Collections.emptyList();
                }

                return new Place(jsonObject.get("place_id").getAsString(),
                        jsonObject.get("name").getAsString(), photos, location);
            }catch (Exception e) {
                throw new JsonParseException(e);
            }
        }
    }
}
