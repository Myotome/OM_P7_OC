package fr.myotome.go4lunch.ui.detailed;

import java.util.List;

import fr.myotome.go4lunch.R;
import fr.myotome.go4lunch.model.User;

public class DetailViewState {

    private final String mRestaurantName;
    private final String mRestaurantAddress;
    private final float mRestaurantRating;
    private final String mUrlRestaurantPhoto;
    private final String mRestaurantPhoneNumber;
    private final String mUrlRestaurantWebsite;
    private final String mPlaceId;
    private final String mOpeningHour;
    private final boolean mFabCurrentRestaurant;
    private final List<User> mCurrentCoworker;
    private final boolean mIsFavorite;

    // TODO MYOTOME attention au naming des variables, presque rien ne correspond entre les variables de ton ViewModel (dans le combine) et ici
    public DetailViewState(String restaurantName,
                           String restaurantAddress,
                           float restaurantRating,
                           String urlRestaurantPhoto,
                           String restaurantPhoneNumber,
                           String urlRestaurantWebsite,
                           String placeId,
                           String openingHour,
                           boolean fabCurrentRestaurant,
                           List<User> currentCoworker,
                           boolean isFavorite) {
        mRestaurantName = restaurantName;
        mRestaurantAddress = restaurantAddress;
        mRestaurantRating = restaurantRating;
        mUrlRestaurantPhoto = urlRestaurantPhoto;
        mRestaurantPhoneNumber = restaurantPhoneNumber;
        mUrlRestaurantWebsite = urlRestaurantWebsite;
        mPlaceId = placeId;
        mOpeningHour = openingHour;
        mFabCurrentRestaurant = fabCurrentRestaurant;
        mCurrentCoworker = currentCoworker;
        mIsFavorite = isFavorite;
    }

    public String getRestaurantName() {
        return mRestaurantName;
    }

    public String getRestaurantAddress() {
        return mRestaurantAddress;
    }

    public float getRestaurantRating() {
        return mRestaurantRating;
    }

    public String getUrlRestaurantPhoto() {
        return mUrlRestaurantPhoto;
    }

    public String getRestaurantPhoneNumber() {
        return mRestaurantPhoneNumber;
    }

    public String getUrlRestaurantWebsite() {
        return mUrlRestaurantWebsite;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public String getOpeningHour() {
        return mOpeningHour;
    }

    // TODO MYOTOME c'est du MVC ça, à retirer (et à calculer dans le VM)
    public int getFabCurrentRestaurant() {
        if(mFabCurrentRestaurant){
            return R.drawable.ic_baseline_check_circle_green_30;
        }
        return R.drawable.ic_baseline_check_circle_24;
    }

    public List<User> getCurrentCoworker() {
        return mCurrentCoworker;
    }

    public boolean isFabCurrentRestaurant() {
        return mFabCurrentRestaurant;
    }

    // TODO MYOTOME suffixer de "drawableRes" et utiliser @DrawableRes pour une meilleure compréhension de ce int
    // TODO MYOTOME same same MVC
    public int getFavoriteRestaurant(){
        if (mIsFavorite){
            return R.drawable.ic_baseline_star_24;
        }
        return R.drawable.ic_baseline_star_outline_24;
    }

    public boolean isFavorite(){
        return mIsFavorite;
    }
}
