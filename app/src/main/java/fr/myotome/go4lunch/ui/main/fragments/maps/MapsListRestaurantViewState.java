package fr.myotome.go4lunch.ui.main.fragments.maps;

import fr.myotome.go4lunch.R;

public class MapsListRestaurantViewState {

    private final double mRestaurantLat;
    private final double mRestaurantLng;
    private final String mPlaceId;
    private final boolean mUserInRestaurant;

    public MapsListRestaurantViewState(double restaurantLat, double restaurantLng, String placeId, boolean userInRestaurant) {
        mRestaurantLat = restaurantLat;
        mRestaurantLng = restaurantLng;
        mPlaceId = placeId;
        mUserInRestaurant = userInRestaurant;
    }

    public double getRestaurantLat() {
        return mRestaurantLat;
    }

    public double getRestaurantLng() {
        return mRestaurantLng;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public int isUserInRestaurant() {
        if (mUserInRestaurant) {
            return R.drawable.baseline_place_booked_24;
        } else {
            return R.drawable.baseline_place_unbook_24;
        }
    }
}
