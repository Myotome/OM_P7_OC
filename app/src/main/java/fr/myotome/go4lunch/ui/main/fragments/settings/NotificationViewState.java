package fr.myotome.go4lunch.ui.main.fragments.settings;

import android.app.Application;

import java.util.List;

import fr.myotome.go4lunch.R;

public class NotificationViewState {

    private final String mName, mRestaurantName;
    private final List<String> mCoworker;
    private final Application mApplication;

    public NotificationViewState(Application application, String name, String restaurantName, List<String> coworker) {
        mApplication = application;
        mName = name;
        mRestaurantName = restaurantName;
        mCoworker = coworker;

    }

    public String getName() {
        return mName;
    }

    private String getRestaurantName() {
        return mRestaurantName;
    }

    private String getCoworker() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String coworker : mCoworker) {
            stringBuilder.append(coworker).append("\n");
        }
        return stringBuilder.toString();
    }

    public String getMessage(){
        return  mApplication.getResources().getText(R.string.hello) + getName() + ".\n"
                + mApplication.getResources().getText(R.string.lunch_notif) + getRestaurantName() + ".\n"
                + mApplication.getResources().getText(R.string.with) + getCoworker();
    }
}
