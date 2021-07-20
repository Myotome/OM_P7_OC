package fr.myotome.go4lunch.ui.main.fragments.workmates;

import android.content.Context;

import fr.myotome.go4lunch.R;

public class WorkmatesViewState {

    private final String mName;
    private final String mUrlPicture;
    private final String mRestaurantName;
    private final Context mContext;
    private final String mPlaceId;

    public WorkmatesViewState(Context context, String name, String urlPicture, String restaurantName, String placeId) {
        mContext = context;
        mName = name;
        mUrlPicture = urlPicture;
        mRestaurantName = restaurantName;
        mPlaceId = placeId;
    }

    public String getName() {
        if(mRestaurantName.equals("")){
            return mName + mContext.getResources().getText(R.string.choice);
        }else {
            return mName + mContext.getResources().getText(R.string.eating);
        }
    }

    public String getUrlPicture() {
        return mUrlPicture;
    }

    public String getRestaurantName() {
        return mRestaurantName;
    }

    public String getPlaceId() {
        return mPlaceId;
    }
}
