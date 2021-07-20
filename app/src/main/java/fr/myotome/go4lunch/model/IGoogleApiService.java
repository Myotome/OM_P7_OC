package fr.myotome.go4lunch.model;


import com.google.android.libraries.places.api.model.AutocompleteSessionToken;

import fr.myotome.go4lunch.model.autocompletePOJO.AutocompletePojo;
import fr.myotome.go4lunch.model.nearbySearchPOJO.NearbySearchPojo;
import fr.myotome.go4lunch.model.detailPlacePOJO.DetailPlacePOJO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGoogleApiService {

    /**
     * API call with retrofit for nearby search
     * @param location user location
     * @param radius in m, around user
     * @param type of query
     * @param key private google key for call
     * @return nearby search POJO
     */

    @GET("nearbysearch/json?")
    Call<NearbySearchPojo> getNearbyByPlace(@Query("location") String location, @Query("radius") int radius, @Query("type") String type, @Query("key") String key);


    /**
     * API call with retrofit for detail place
     * @param placeId unique
     * @param key private google key for call
     * @return detail place POJO
     */
    @GET("details/json?")
    Call<DetailPlacePOJO> getDetailPlaceSearch(@Query("placeid") String placeId, @Query("key") String key);


    /**
     * API call with retrofit for autocomplete prediction
     * @param input string to query
     * @param location user location
     * @param radius in m, around user
     * @param key private google key for call
     * @param token to limit price of call
     * @return autocomplete POJO
     */
    @GET("autocomplete/json?types=establishment&offset=3&strictbounds")
    Call<AutocompletePojo> getAutocompleteResult (@Query("input") String input,
                                                  @Query("location") String location,
                                                  @Query("radius") int radius,
                                                  @Query("key") String key,
                                                  @Query("token") AutocompleteSessionToken token);



}
