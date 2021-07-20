package fr.myotome.go4lunch.ui.main.fragments.workmates;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import fr.myotome.go4lunch.model.User;
import fr.myotome.go4lunch.repository.FirebaseRepository;

public class WorkmatesViewModel extends AndroidViewModel {

    private final MediatorLiveData<List<WorkmatesViewState>> mWorkmatesList = new MediatorLiveData<>();
    private final Application mApplication;

    public WorkmatesViewModel(@NonNull @NotNull Application application, FirebaseRepository firebaseRepository) {
        super(application);
        mApplication = application;
        LiveData<List<User>> getAllUsers = firebaseRepository.getAllUsers();

        mWorkmatesList.addSource(getAllUsers, this::transformData);

    }

    /**
     * Main method. all the parameters allow to create a model which will dispense the views
     * At the end a view state is created with basic fields for display correct data
     * @param userList from fireStore, list of coworker
     */
    private void transformData(@Nullable List<User> userList) {
        if (userList == null) {
            return;
        }
        List<WorkmatesViewState> modelList = new ArrayList<>();
        for (User user : userList) {
            String restaurantName = "";
            String restaurantId = "";

            if (user.getRestaurantName() != null) {
                restaurantName = user.getRestaurantName();
            }
            if (user.getRestaurantPlaceId() != null) {
                restaurantId = user.getRestaurantPlaceId();
            }
            modelList.add(new WorkmatesViewState(mApplication, user.getFirstName(), user.getUrlPicture(), restaurantName, restaurantId));
            mWorkmatesList.setValue(modelList);
        }
    }

    public LiveData<List<WorkmatesViewState>> getWorkmatesList() {
        return mWorkmatesList;
    }
}
