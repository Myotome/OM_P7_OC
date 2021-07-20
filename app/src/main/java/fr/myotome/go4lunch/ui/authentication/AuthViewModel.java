package fr.myotome.go4lunch.ui.authentication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.AuthCredential;

import org.jetbrains.annotations.NotNull;

import fr.myotome.go4lunch.model.User;
import fr.myotome.go4lunch.repository.FirebaseRepository;

public class AuthViewModel extends AndroidViewModel {

    private final FirebaseRepository mAuthRepository;

    public AuthViewModel(@NonNull @NotNull Application application, FirebaseRepository firebaseRepository) {
        super(application);
        mAuthRepository = firebaseRepository;

    }

    public void signInWithCredential(AuthCredential credential){
       mAuthRepository.signInWithFirebaseCredential(credential);
    }

    public void createUserWithEmail(String name, String email, String password){
       mAuthRepository.createUserWithEmailAndPassword(name, email,password);
    }

    public void signInWithEmailAndPassword(String email, String password){
        mAuthRepository.signInWithEmailAndPassword(email, password);

    }

    public LiveData<User> getSignInMutableLiveData() {
        return mAuthRepository.getUserMutableLiveData();
    }

}
