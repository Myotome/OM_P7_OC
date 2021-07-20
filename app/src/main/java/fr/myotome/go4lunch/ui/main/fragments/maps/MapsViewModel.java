package fr.myotome.go4lunch.ui.main.fragments.maps;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import fr.myotome.go4lunch.model.User;
import fr.myotome.go4lunch.model.autocompletePOJO.AutocompletePojo;
import fr.myotome.go4lunch.model.autocompletePOJO.Prediction;
import fr.myotome.go4lunch.model.nearbySearchPOJO.NearbySearchPojo;
import fr.myotome.go4lunch.model.nearbySearchPOJO.Result;
import fr.myotome.go4lunch.repository.FirebaseRepository;
import fr.myotome.go4lunch.repository.MainRepository;

public class MapsViewModel extends AndroidViewModel {

    private final MediatorLiveData<MapsViewState> mMediatorLiveData = new MediatorLiveData<>();


    public MapsViewModel(Application application, MainRepository mainRepository, FirebaseRepository firebaseRepository) {
        super(application);

        LiveData<NearbySearchPojo> queryNearbySearch = mainRepository.getNearbySearchQuery();
        LiveData<LatLng> currentPosition = mainRepository.getCurrentLocation();
        LiveData<List<User>> workmatesInRestaurant = firebaseRepository.getAllUsers();
        LiveData<AutocompletePojo> queryAutocomplete = mainRepository.getAutocompleteQuery();


        mMediatorLiveData.addSource(currentPosition, latLng ->
                combine(latLng,
                queryNearbySearch.getValue(),
                workmatesInRestaurant.getValue(),
                queryAutocomplete.getValue()));

        mMediatorLiveData.addSource(queryNearbySearch, nearbySearchPojo ->
                combine(currentPosition.getValue(),
                nearbySearchPojo,
                workmatesInRestaurant.getValue(),
                null));

        mMediatorLiveData.addSource(workmatesInRestaurant, users ->
                combine(currentPosition.getValue(),
                queryNearbySearch.getValue(),
                users,
                queryAutocomplete.getValue()));

        mMediatorLiveData.addSource(queryAutocomplete, autocompletePojo ->
                combine(currentPosition.getValue(),
                queryNearbySearch.getValue(),
                workmatesInRestaurant.getValue(),
                autocompletePojo));

    }

    /**
     * Main method. all the parameters allow to create a model which will dispense the views
     * At the end a view state is created with basic fields for display correct data
     * @param currentPosition of device from live data
     * @param queryNearbySearch from nearby search with live data
     * @param listOfCoworker from fireStore with live data
     * @param queryAutoComplete prediction of autocomplete with live data
     */
    private void combine(@Nullable LatLng currentPosition,
                         @Nullable NearbySearchPojo queryNearbySearch,
                         @Nullable List<User> listOfCoworker,
                         @Nullable AutocompletePojo queryAutoComplete) {

        if (currentPosition == null || queryNearbySearch == null) {
            return;
        }
        double restaurantLat = 0, restaurantLng = 0;
        String restaurantPlaceId = "";


        List<MapsListRestaurantViewState> listRestaurant = new ArrayList<>();

        for (Result result : queryNearbySearch.getResults()) {
            boolean userInRestaurant = false;

            if (controlFilterByPrediction(result, queryAutoComplete)
                    || queryAutoComplete == null
                    || queryAutoComplete.getPredictions().size() == 0) {

                restaurantLat = result.getGeometry().getLocation().getLat();
                restaurantLng = result.getGeometry().getLocation().getLng();
                restaurantPlaceId = result.getPlaceId();
            }

            if (listOfCoworker != null) {
                for (User user : listOfCoworker) {
                    if (user.getRestaurantPlaceId() != null) {
                        if (user.getRestaurantPlaceId().equals(restaurantPlaceId)) {
                            userInRestaurant = true;
                            break;
                        }
                    }
                }
            }

            listRestaurant.add(new MapsListRestaurantViewState(restaurantLat, restaurantLng, restaurantPlaceId, userInRestaurant));
        }


        mMediatorLiveData.setValue(new MapsViewState(currentPosition.latitude, currentPosition.longitude, listRestaurant));

    }

    /**
     * Allows to filter restaurants by predictions
     * @param result from nearby search
     * @param queryAuto get predictions
     * @return if prediction not null, permit to return true for restaurant in prediction
     */
    private boolean controlFilterByPrediction(Result result, AutocompletePojo queryAuto) {
        boolean contains = false;
        if (queryAuto != null && queryAuto.getPredictions().size() != 0) {
            for (Prediction prediction : queryAuto.getPredictions()) {
                if (prediction.getPlaceId().equals(result.getPlaceId())) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    public LiveData<MapsViewState> getMediatorLiveData() {
        return mMediatorLiveData;
    }


}

