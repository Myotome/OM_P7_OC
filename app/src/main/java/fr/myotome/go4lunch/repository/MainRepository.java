package fr.myotome.go4lunch.repository;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.LruCache;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;

import org.jetbrains.annotations.NotNull;

import fr.myotome.go4lunch.BuildConfig;
import fr.myotome.go4lunch.model.IGoogleApiService;
import fr.myotome.go4lunch.model.autocompletePOJO.AutocompletePojo;
import fr.myotome.go4lunch.model.detailPlacePOJO.DetailPlacePOJO;
import fr.myotome.go4lunch.model.nearbySearchPOJO.NearbySearchPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// TODO MYOTOME A découper en plusieurs parties (autant de thème / API que nécessaire)
// TODO MYOTOME Non testé !
public class MainRepository {

    private final Application mApplication;

    // TODO MYOTOME Un repo doit être stateless, à supprimer (les 3)
    private final MutableLiveData<NearbySearchPojo> queryNearbySearchLiveData = new MutableLiveData<>();
    private final MutableLiveData<DetailPlacePOJO> queryDetailPlaceLiveData = new MutableLiveData<>();
    private final MutableLiveData<AutocompletePojo> queryAutocompleteLiveData = new MutableLiveData<>();
    // TODO MYOTOME Elle, elle est cool : c'est un état global constant que tu exposes, donc c'est bien de la garder "au chaud" :D
    private final MutableLiveData<LatLng> currentPosition = new MutableLiveData<>();

    // TODO MYOTOME A supprimer (il faut que tu réagisses à ton location, c'est à dire à la LiveData)
    private String mLoc = null;
    // TODO MYOTOME Injecte directement ton service (IGoogleApiService) plutôt que de le créer à chaque requête
    private Retrofit mRetrofit;
    private final LruCache<String, DetailPlacePOJO> detailCache = new LruCache<>(2000);
    // TODO MYOTOME A supprimer : ce n'est pas le rôle d'un Repo que de demander les infos d'un autre repo,
    //  c'est le ViewModel qui doit s'en charger
    private final SharedPreferences mPreferences;
    // TODO MYOTOME Fait un petit repon "autour" des SharedPrefs pour mettre l'accès et la sauvegarde de cette donnée
    private static final String RADIUS = "radius";


    public MainRepository(Application application) {
        mApplication = application;
        // TODO MYOTOME naming
        String SHARED_PREFERENCES = "SharedPreferences";
        mPreferences = application.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

    }

    /**
     * Base for retrofit call
     */
    // TODO MYOTOME A faire qu'une fois, dans ta DI / ViewModelFactory
    private void buildRetrofit() {

        String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        mRetrofit = builder.build();
    }

    //---------------Location--------------//
    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(mApplication, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mApplication, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // TODO MYOTOME A protéger pour éviter de faire plusieurs fois la requête
            LocationRequest request = new LocationRequest();
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            request.setInterval(10000); //10 sec
            request.setFastestInterval(5000); //5 sec
            request.setSmallestDisplacement(100); //100 m
            LocationServices.getFusedLocationProviderClient(mApplication)
                    .requestLocationUpdates(request, callback, Looper.getMainLooper());

        } else {
            // TODO MYOTOME Pas dans le repo, c'est le ViewModel qui doit se charger de faire afficher un toast
            Toast.makeText(mApplication, "Permission request to show map", Toast.LENGTH_SHORT).show();
        }

    }

    private final LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            // TODO MYOTOME super non nécessaire
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double currentLat = locationResult.getLastLocation().getLatitude();
                double currentLng = locationResult.getLastLocation().getLongitude();
                mLoc = currentLat + "," + currentLng;
                currentPosition.setValue(new LatLng(currentLat, currentLng));
                // TODO MYOTOME C'est côté VM ça
                setNearbySearchQuery();
            }
        }
    };

    public LiveData<LatLng> getCurrentLocation() {
        if (currentPosition.getValue() == null) {
            // TODO MYOTOME On préférerait un mécanisme global à l'application (dans la Main/BaseActivity pour savoir s'il y a besoin de
            //  lancer la recherche GPS
            getDeviceLocation();
        }
        return currentPosition;

    }

    //----------------API queries------------//
    // TODO MYOTOME Ca va devenir un "get"
    public void setNearbySearchQuery() {
        if (mLoc == null) {
            getDeviceLocation();
        }
        buildRetrofit();
        mRetrofit.create(IGoogleApiService.class).getNearbyByPlace(mLoc, mPreferences.getInt(RADIUS, 300), "restaurant", BuildConfig.MAPS_API_KEY)
                .enqueue(new Callback<NearbySearchPojo>() {
                    @Override
                    public void onResponse(@NotNull Call<NearbySearchPojo> call, @NotNull Response<NearbySearchPojo> response) {
                        if (response.isSuccessful()) {
                            queryNearbySearchLiveData.setValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<NearbySearchPojo> call, @NotNull Throwable t) {
                        queryNearbySearchLiveData.setValue(null);
                    }
                });
    }

    // TODO MYOTOME Un repo doit être stateless, à supprimer (renvoie ta livedata dans ton setNearbySearchQuery)
    public LiveData<NearbySearchPojo> getNearbySearchQuery() {
        return queryNearbySearchLiveData;
    }

    // TODO MYOTOME Attention au naming, c'est plutôt dans l'autre sens qu'il faut le voir : queryPlaceDetails(placeId)
    //  On "donne des ordres" au repository, il est intelligent (il fait du cache, du loadbalancing, etc) mais ne choisit rien
    public void setDetailPlaceQuery(String placeId) {

        DetailPlacePOJO existing = detailCache.get(placeId);

        if (existing != null) {
            queryDetailPlaceLiveData.setValue(existing);
        } else {

            buildRetrofit();
            mRetrofit.create(IGoogleApiService.class).getDetailPlaceSearch(placeId, BuildConfig.MAPS_API_KEY)
                    .enqueue(new Callback<DetailPlacePOJO>() {
                        @Override
                        public void onResponse(@NotNull Call<DetailPlacePOJO> call, @NotNull Response<DetailPlacePOJO> response) {
                            DetailPlacePOJO body = response.body();

                            if (response.isSuccessful() && body != null) {
                                detailCache.put(placeId, body);
                            }
                            queryDetailPlaceLiveData.setValue(body);
                        }

                        @Override
                        public void onFailure(@NotNull Call<DetailPlacePOJO> call, @NotNull Throwable t) {
                            t.printStackTrace();
                        }
                    });
        }
    }

    // TODO MYOTOME Un repo doit être stateless, à supprimer (renvoie ta livedata dans ton setDetailPlaceQuery)
    public LiveData<DetailPlacePOJO> getDetailPlaceQuery() {
        return queryDetailPlaceLiveData;
    }

    // TODO MYOTOME same same
    public void setAutocompleteQuery(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        if (mLoc == null) {
            getDeviceLocation();
        }
        buildRetrofit();
        mRetrofit.create(IGoogleApiService.class).getAutocompleteResult(query, mLoc, mPreferences.getInt(RADIUS, 300), BuildConfig.MAPS_API_KEY, token)
                .enqueue(new Callback<AutocompletePojo>() {
                    @Override
                    public void onResponse(@NotNull Call<AutocompletePojo> call, @NotNull Response<AutocompletePojo> response) {
                        if (response.body() != null) {
                            queryAutocompleteLiveData.setValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<AutocompletePojo> call, @NotNull Throwable t) {
                        queryAutocompleteLiveData.setValue(null);
                    }
                });
    }

    // TODO MYOTOME Je pense qu'il est intéressant de dissocier une réponse serveur d'une interaction utilisateur.
    //  Il te faut 2 LiveData ici je pense
    public void setAutocompleteNull() {
        queryAutocompleteLiveData.setValue(null);
    }

    // TODO MYOTOME Un repo doit être stateless, à supprimer (renvoie ta livedata dans ton setAutocompleteQuery)
    public LiveData<AutocompletePojo> getAutocompleteQuery() {
        return queryAutocompleteLiveData;
    }


}
