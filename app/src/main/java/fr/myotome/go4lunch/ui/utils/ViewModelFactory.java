package fr.myotome.go4lunch.ui.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import fr.myotome.go4lunch.ui.main.fragments.settings.NotificationViewModel;
import fr.myotome.go4lunch.repository.FirebaseRepository;
import fr.myotome.go4lunch.repository.MainRepository;
import fr.myotome.go4lunch.ui.authentication.AuthViewModel;
import fr.myotome.go4lunch.ui.detailed.DetailViewModel;
import fr.myotome.go4lunch.ui.main.MainViewModel;
import fr.myotome.go4lunch.ui.main.fragments.listView.ListViewModel;
import fr.myotome.go4lunch.ui.main.fragments.maps.MapsViewModel;
import fr.myotome.go4lunch.ui.main.fragments.workmates.WorkmatesViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory sFactory;
    private final FirebaseRepository mFirebaseRepository;
    private final MainRepository mMainRepository;

    public ViewModelFactory(FirebaseRepository firebaseRepository, MainRepository mainRepository) {
        mFirebaseRepository = firebaseRepository;
        mMainRepository = mainRepository;
    }

    public static ViewModelFactory getInstance(){
        if(sFactory == null){
            synchronized (ViewModelFactory.class){
                if(sFactory == null){
                    sFactory = new ViewModelFactory(new FirebaseRepository(MainApplication.getInstance()), new MainRepository(MainApplication.getInstance()));
                }
            }
        }
        return sFactory;
    }



    @SuppressWarnings("unchecked")
    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(MainViewModel.class)){
            return (T) new MainViewModel(MainApplication.getInstance(), mMainRepository, mFirebaseRepository);
        }else if(modelClass.isAssignableFrom(MapsViewModel.class)){
            return (T) new MapsViewModel(MainApplication.getInstance(), mMainRepository, mFirebaseRepository);
        }else if(modelClass.isAssignableFrom(DetailViewModel.class)){
            return (T) new DetailViewModel(MainApplication.getInstance(), mMainRepository, mFirebaseRepository);
        }else if (modelClass.isAssignableFrom(AuthViewModel.class)){
            return (T) new AuthViewModel(MainApplication.getInstance(), mFirebaseRepository);
        }else if (modelClass.isAssignableFrom(ListViewModel.class)){
            return (T) new ListViewModel(MainApplication.getInstance(), mMainRepository, mFirebaseRepository);
        }else if(modelClass.isAssignableFrom(WorkmatesViewModel.class)){
            return (T) new WorkmatesViewModel(MainApplication.getInstance(), mFirebaseRepository);
        }else if (modelClass.isAssignableFrom(NotificationViewModel.class)){
            return (T) new NotificationViewModel(MainApplication.getInstance(), mFirebaseRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
