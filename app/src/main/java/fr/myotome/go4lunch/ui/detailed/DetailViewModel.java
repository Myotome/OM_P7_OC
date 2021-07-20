package fr.myotome.go4lunch.ui.detailed;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.List;

import fr.myotome.go4lunch.R;
import fr.myotome.go4lunch.model.FavoriteRestaurant;
import fr.myotome.go4lunch.model.User;
import fr.myotome.go4lunch.model.detailPlacePOJO.DetailPlacePOJO;
import fr.myotome.go4lunch.model.detailPlacePOJO.OpeningHours;
import fr.myotome.go4lunch.model.detailPlacePOJO.Period;
import fr.myotome.go4lunch.model.detailPlacePOJO.Result;
import fr.myotome.go4lunch.repository.FirebaseRepository;
import fr.myotome.go4lunch.repository.MainRepository;

public class DetailViewModel extends AndroidViewModel {

    private final MainRepository mMainRepository;
    private final FirebaseRepository mFirebaseRepository;
    private final Application mApplication;
    private final MediatorLiveData<DetailViewState> mMediatorLiveData = new MediatorLiveData<>();

    public DetailViewModel(Application application, MainRepository mainRepository, FirebaseRepository firebaseRepository) {
        super(application);
        mMainRepository = mainRepository;
        mApplication = application;
        mFirebaseRepository = firebaseRepository;

        LiveData<DetailPlacePOJO> getDetailPlacePojo = mainRepository.getDetailPlaceQuery();
        LiveData<List<User>> getAllUsers = firebaseRepository.getAllUsers();
        LiveData<User> getCurrentUser = firebaseRepository.getCurrentUser();
        LiveData<List<FavoriteRestaurant>> getAllFavoriteRestaurant = firebaseRepository.getAllFavoritesRestaurantsForCurrentUser();

        mMediatorLiveData.addSource(getDetailPlacePojo, detailPlacePOJO ->
                combine(detailPlacePOJO,
                getAllUsers.getValue(),
                getCurrentUser.getValue(),
                getAllFavoriteRestaurant.getValue()));

        mMediatorLiveData.addSource(getAllUsers, userList ->
                combine(getDetailPlacePojo.getValue(),
                userList,
                getCurrentUser.getValue(),
                getAllFavoriteRestaurant.getValue()));

        mMediatorLiveData.addSource(getCurrentUser, user ->
                combine(getDetailPlacePojo.getValue(),
                getAllUsers.getValue(),
                user,
                getAllFavoriteRestaurant.getValue()));

        mMediatorLiveData.addSource(getAllFavoriteRestaurant, favoriteRestaurants ->
                combine(getDetailPlacePojo.getValue(),
                getAllUsers.getValue(),
                getCurrentUser.getValue(),
                favoriteRestaurants));
    }

    public void queryToDetailPlace(String placeId) {
        mMainRepository.setDetailPlaceQuery(placeId);
    }

    public void callRestaurant(String internationalPhoneNumber) {

        if (internationalPhoneNumber != null && !internationalPhoneNumber.equals("")) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            callIntent.setData(Uri.parse("tel:" + internationalPhoneNumber));
            mApplication.startActivity(callIntent);
        } else {
            Toast.makeText(mApplication, getApplication().getResources().getText(R.string.no_phone), Toast.LENGTH_SHORT).show();
        }
    }

    public void openRestaurantWebsite(String website) {

        if (website != null && !website.equals("")) {
            Intent openWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
            openWebsite.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApplication.startActivity(openWebsite);
        } else {
            Toast.makeText(mApplication, getApplication().getResources().getText(R.string.no_website), Toast.LENGTH_SHORT).show();
        }
    }

    public void setRestaurantForLunch(String placeId, String restaurantName, boolean isActualRestaurant) {
        if (!isActualRestaurant) {
            mFirebaseRepository.setRestaurantForLunch(placeId, restaurantName);
        } else {
            mFirebaseRepository.setRestaurantForLunch("", "");
        }
    }

    public void setFavoriteRestaurant(String placeId, String name, String url, boolean isFavorite) {
        if (!isFavorite) {
            mFirebaseRepository.setFavoriteRestaurantForCurrentUser(new FavoriteRestaurant(placeId, name, url));
        } else {
            mFirebaseRepository.deleteFavoriteRestaurantForCurrentUser(new FavoriteRestaurant(placeId, name, url));
        }
    }

    public LiveData<DetailViewState> getMediatorLiveData() {
        return mMediatorLiveData;
    }


    /**
     * Main method. all the parameters allow to create a model which will dispense the views
     * At the end a view state is created with basic fields for display correct data
     * @param valueOfDetailPlace get details with live data
     * @param listOfCoworker get list of coworker with live data
     * @param currentUser get current user with live data
     * @param favoriteRestaurantList get list of favorite restaurant for current user with live data
     */
    private void combine(@Nullable DetailPlacePOJO valueOfDetailPlace,
                         @Nullable List<User> listOfCoworker,
                         User currentUser,
                         @Nullable List<FavoriteRestaurant> favoriteRestaurantList) {
        if (valueOfDetailPlace == null || currentUser == null) {
            return;
        }
        Result result = valueOfDetailPlace.getResult();
        float rating = 0;
        String urlPhoto = "", phoneNumber = "", website = "", openingHour = "";
        boolean actualChoice = false, isFavorite = false;
        List<User> coworkerInThisRestaurant = new ArrayList<>();

        if (result.getRating() != null) {
            rating = (float) ((result.getRating() * 3) / 5);
        }
        if (result.getPhotos() != null) {
            urlPhoto = result.getPhotos().get(0).getPhotoReference();
        }
        if (result.getFormattedPhoneNumber() != null) {
            phoneNumber = result.getFormattedPhoneNumber();
        }
        if (result.getWebsite() != null) {
            website = result.getWebsite();
        }
        if (currentUser.getRestaurantPlaceId() != null && currentUser.getRestaurantPlaceId().equals(result.getPlaceId())) {
            actualChoice = true;
        }

        if (result.getOpeningHours() != null) {
            openingHour = getOpeningHour(result.getOpeningHours());
        }

        if (listOfCoworker != null) {
            for (User user : listOfCoworker) {
                if (user.getRestaurantPlaceId() != null) {
                    if (user.getRestaurantPlaceId().equals(result.getPlaceId())) {
                        coworkerInThisRestaurant.add(user);
                    }
                }
            }
        }

        if (favoriteRestaurantList != null && !favoriteRestaurantList.isEmpty()) {
            isFavorite = getFavoriteRestaurant(favoriteRestaurantList, valueOfDetailPlace.getResult().getPlaceId());
        }

        mMediatorLiveData.setValue(new DetailViewState(result.getName(),
                result.getVicinity(),
                rating,
                urlPhoto,
                phoneNumber,
                website,
                result.getPlaceId(),
                openingHour,
                actualChoice,
                coworkerInThisRestaurant,
                isFavorite
        ));

    }

    private boolean getFavoriteRestaurant(List<FavoriteRestaurant> favoriteRestaurantList, String currentPlaceId) {
        for (FavoriteRestaurant fav : favoriteRestaurantList) {
            if (fav.getPlaceId().equals(currentPlaceId)) {
                return true;
            }
        }
        return false;
    }

    private String getOpeningHour(OpeningHours hours) {
        if (hours.getOpenNow()) {
            for (Period period : hours.getPeriods()) {
                StringBuilder stringBuilder = new StringBuilder(period.getClose().getTime());
                stringBuilder.insert(2, "h");

                return mApplication.getResources().getText(R.string.open_until) + stringBuilder.toString();

            }
        }
        return mApplication.getResources().getText(R.string.close).toString();
    }

}
