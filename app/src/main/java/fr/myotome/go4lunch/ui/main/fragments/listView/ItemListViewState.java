package fr.myotome.go4lunch.ui.main.fragments.listView;

import androidx.annotation.Nullable;

public class ItemListViewState {

    private final String mRestaurantName;
    private final String mRestaurantAddress;
    private final CharSequence mRestaurantOpening;
    private final String mDistance;
    private final String mHowManyWorkmate;
    @Nullable
    private final String mRestaurantPictureUrl;
    private final float mRestaurantRating;
    private final String mPlaceId;

    public ItemListViewState(String restaurantName,
                             String restaurantAddress,
                             CharSequence restaurantOpening,
                             String distance,
                             String howManyWorkmate,
                             @Nullable String restaurantPictureUrl,
                             float restaurantRating,
                             String placeId) {

        mRestaurantName = restaurantName;
        mRestaurantAddress = restaurantAddress;
        mRestaurantOpening = restaurantOpening;
        mDistance = distance;
        mHowManyWorkmate = howManyWorkmate;
        mRestaurantPictureUrl = restaurantPictureUrl;
        mRestaurantRating = restaurantRating;
        mPlaceId = placeId;
    }

    public String getRestaurantName() {
        return mRestaurantName;
    }

    public String getRestaurantAddress() {
        return mRestaurantAddress;
    }

    public CharSequence getRestaurantOpening() {
        return mRestaurantOpening;
    }

    public String getDistance() {
        return mDistance;
    }

    public String getHowManyWorkmate() {
        return mHowManyWorkmate;
    }

    @Nullable
    public String getRestaurantPictureUrl() {
        return mRestaurantPictureUrl;
    }

    public float getRestaurantRating() {
        return mRestaurantRating;
    }

    public String getPlaceId() {
        return mPlaceId;
    }
}
