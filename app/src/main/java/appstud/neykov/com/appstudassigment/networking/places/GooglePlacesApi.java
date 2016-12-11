package appstud.neykov.com.appstudassigment.networking.places;

import appstud.neykov.com.appstudassigment.networking.places.Location;
import appstud.neykov.com.appstudassigment.networking.places.PlacesSearchResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesApi {

    @GET("nearbysearch/json")
    Call<PlacesSearchResponse> searchPlaces(@Query("key") String apiKey, @Query("location") Location location, @Query("radius") int radiusMeters, @Query("type") String type);

    @GET("photo")
    Call<ResponseBody> getPhoto(@Query("key") String apiKey, @Query("photoreference") String photoReference, @Query("maxWidth") int maxWidth, @Query("maxHeight") int maxHeight);
}

