package fr.myotome.go4lunch.ui.main.fragments.listView;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import fr.myotome.go4lunch.R;
import fr.myotome.go4lunch.model.User;
import fr.myotome.go4lunch.model.autocompletePOJO.AutocompletePojo;
import fr.myotome.go4lunch.model.autocompletePOJO.Prediction;
import fr.myotome.go4lunch.model.nearbySearchPOJO.NearbySearchPojo;
import fr.myotome.go4lunch.model.nearbySearchPOJO.Result;
import fr.myotome.go4lunch.repository.FirebaseRepository;
import fr.myotome.go4lunch.repository.MainRepository;

// TODO MYOTOME pas testé
// TODO MYOTOME pas besoin d'étendre de AndroidViewModel, ViewModel suffit
public class ListViewModel extends AndroidViewModel {

    private final MediatorLiveData<ListViewState> mMediatorLiveData = new MediatorLiveData<>();
    private final Application mApplication;
    // TODO MYOTOME same same 1 ligne / variable
    // TODO MYOTOME en fait non faut même les supprimer ces variables, une méthode doit renvoyer l'objet complet plutôt que de remplir ces fields
    String mName = "", mAddress = "", mDistance = "", mWorkmates = "0", mPicture = "", mPlaceId = "";
    CharSequence mOpening = "";
    float mRating = 0;


    public ListViewModel(@NonNull @NotNull Application application, MainRepository mainRepository, FirebaseRepository firebaseRepository) {
        super(application);
        mApplication = application;

        LiveData<NearbySearchPojo> queryNearbySearch = mainRepository.getNearbySearchQuery();
        LiveData<List<User>> usersList = firebaseRepository.getAllUsers();
        LiveData<LatLng> currentPosition = mainRepository.getCurrentLocation();
        LiveData<AutocompletePojo> queryAutocomplete = mainRepository.getAutocompleteQuery();

        mMediatorLiveData.addSource(queryNearbySearch, nearbySearchPojo ->
                combine(nearbySearchPojo,
                usersList.getValue(),
                currentPosition.getValue(),
                null));

        mMediatorLiveData.addSource(usersList, userList ->
                combine(queryNearbySearch.getValue(),
                userList,
                currentPosition.getValue(),
                queryAutocomplete.getValue()));

        mMediatorLiveData.addSource(currentPosition, latLng ->
                combine(queryNearbySearch.getValue(),
                usersList.getValue(),
                latLng,
                queryAutocomplete.getValue()));

        mMediatorLiveData.addSource(queryAutocomplete, autocompletePojo ->
                combine(queryNearbySearch.getValue(),
                usersList.getValue(),
                currentPosition.getValue(),
                autocompletePojo));

    }

    // TODO MYOTOME same same naming
    public LiveData<ListViewState> getMediatorLiveData() {
        return mMediatorLiveData;
    }


    /**
     * Main method. all the parameters allow to create a model which will dispense the views
     * At the end a view state is created with basic fields for display correct data
     * @param nearbySearchPojo get nearby search with live data
     * @param userList get list of coworker with live data
     * @param currentPosition get current position with live data
     * @param autocompletePojo get prediction by autocomplete with live data
     */
    private void combine(@Nullable NearbySearchPojo nearbySearchPojo,
                         @Nullable List<User> userList,
                         @Nullable LatLng currentPosition,
                         @Nullable AutocompletePojo autocompletePojo) {

        List<ItemListViewState> itemListViewStates = new ArrayList<>();

        if (nearbySearchPojo != null) {
            for (Result nearbyResult : nearbySearchPojo.getResults()) {

                if (controlFilterByPrediction(nearbyResult, autocompletePojo)
                        || autocompletePojo == null
                        || autocompletePojo.getPredictions().size()==0) {


                    manipulateDataForCorrectUse(nearbyResult);
                    obtainDistance(nearbyResult, currentPosition);
                    getHowManyWorkmateInRestaurant(nearbyResult.getPlaceId(), userList);

                    itemListViewStates.add(
                            new ItemListViewState(mName,
                                    mAddress,
                                    mOpening,
                                    mDistance,
                                    mWorkmates,
                                    mPicture,
                                    mRating,
                                    mPlaceId));
                }
            }


            mMediatorLiveData.setValue(new ListViewState(itemListViewStates));
        }
    }

    /**
     * Allows to filter restaurants by predictions
     * @param result from nearby search
     * @param autocompletePojo get prediction
     * @return if prediction not null, permit to return true for restaurant in prediction
     */
    private boolean controlFilterByPrediction(Result result, AutocompletePojo autocompletePojo){
        boolean contains = false;
        // TODO MYOTOME pas besoin du size() != 0
        if(autocompletePojo != null && autocompletePojo.getPredictions().size()!=0){
            for (Prediction prediction : autocompletePojo.getPredictions()){
                if (prediction.getPlaceId().equals(result.getPlaceId())) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    /**
     * Allows to check the existence conditions for each field
     * @param result from nearby search
     */
    private void manipulateDataForCorrectUse(Result result) {
        if (result.getName() != null) {
            mName = result.getName();
        }
        if (result.getVicinity() != null) {
            mAddress = result.getVicinity();
        }
        if (result.getOpeningHours() != null) {
            // TODO MYOTOME insuffisant, il faut dire à quelle heure l'établissement ferme / ouvre
            if (result.getOpeningHours().getOpenNow()) {
                mOpening = mApplication.getResources().getText(R.string.open);
            } else {
                mOpening = mApplication.getResources().getText(R.string.close);
            }
        }
        if (result.getPhotos() != null) {
            mPicture = result.getPhotos().get(0).getPhotoReference();
        }
        if (result.getRating() != null) {
            mRating = (float) ((result.getRating() * 3) / 5);
        }
        if (result.getPlaceId() != null) {
            mPlaceId = result.getPlaceId();
        }

    }

    /**
     * Allows to calculate the distance between the user and the restaurant
     * @param nearbyResult from nearby search
     * @param currentPosition from device position
     */
    // TODO MYOTOME return une String
    private void obtainDistance(Result nearbyResult, LatLng currentPosition) {
        if (nearbyResult.getGeometry() != null && currentPosition != null) {

            Location nearbyLocation = new Location("nearbyRestaurant");
            nearbyLocation.setLatitude(nearbyResult.getGeometry().getLocation().getLat());
            nearbyLocation.setLongitude(nearbyResult.getGeometry().getLocation().getLng());

            Location currentLocation = new Location("currentLocation");
            currentLocation.setLatitude(currentPosition.latitude);
            currentLocation.setLongitude(currentPosition.longitude);

            float distance = Math.round(currentLocation.distanceTo(nearbyLocation));

            mDistance = distance + " m";
        }
    }

    /**
     * Allows to know the number of coworkers in this restaurant
     * @param placeId from nearby search, specific
     * @param userList from fireStore
     */
    // TODO MYOTOME return String
    private void getHowManyWorkmateInRestaurant(String placeId, @Nullable List<User> userList) {
        int howMany = 0;
        if (placeId != null && userList != null) {

            for (User user : userList) {
                if (user.getRestaurantPlaceId() != null) {
                    if (user.getRestaurantPlaceId().equals(placeId)) {
                        howMany++;
                    }
                }
            }
        }
        mWorkmates = String.valueOf(howMany);
    }

}
