package fr.myotome.go4lunch.ui.main;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import fr.myotome.go4lunch.model.User;
import fr.myotome.go4lunch.repository.FirebaseRepository;
import fr.myotome.go4lunch.repository.MainRepository;

public class MainViewModel extends AndroidViewModel {

    private final MainRepository mMainRepository;
    private final FirebaseRepository mFirebaseRepository;
    private final MediatorLiveData<MainViewState> mMediatorData = new MediatorLiveData<>();


    public MainViewModel(Application application, MainRepository mainRepository, FirebaseRepository firebaseRepository) {
        super(application);
        mMainRepository = mainRepository;
        mFirebaseRepository = firebaseRepository;

        LiveData<User> getCurrentUser = firebaseRepository.getCurrentUser();

        mMediatorData.addSource(getCurrentUser, this::combine);

    }

    /**
     * Main method. all the parameters allow to create a model which will dispense the views
     * At the end a view state is created with basic fields for display correct data
     * @param value current user with live data
     */
    private void combine(@Nullable User value ) {
        if (value == null) {
            return;
        }

        String urlPicture = "";
        if (value.getUrlPicture() != null) {
            urlPicture = value.getUrlPicture();
        }
        mMediatorData.setValue(new MainViewState(value.getFirstName(), urlPicture, value.getMail(), value.getRestaurantPlaceId()));
    }

    public void setQuery() {
        mMainRepository.setNearbySearchQuery();
    }

    public MediatorLiveData<MainViewState> getMediatorData() {
        return mMediatorData;
    }

    public void setAutocompleteQuery(String query) {
        mMainRepository.setAutocompleteQuery(query);
    }

    public void setNullUser(){
        mFirebaseRepository.setNullUser();
    }

    public void setAutocompleteNull(){
     mMainRepository.setAutocompleteNull();
    }

}
