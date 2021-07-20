package fr.myotome.go4lunch.ui.main.fragments.maps;

import java.util.List;

public class MapsViewState {

    private final double mCurrentLat;
    private final double mCurrentLng;
    private final List<MapsListRestaurantViewState> mListRestaurantViewStates;

    public MapsViewState(double currentLat, double currentLng, List<MapsListRestaurantViewState> listRestaurantViewStates) {
        mCurrentLat = currentLat;
        mCurrentLng = currentLng;
        mListRestaurantViewStates = listRestaurantViewStates;
    }

    public double getCurrentLat() {
        return mCurrentLat;
    }

    public double getCurrentLng() {
        return mCurrentLng;
    }

    public List<MapsListRestaurantViewState> getListRestaurantViewStates() {
        return mListRestaurantViewStates;
    }

}
