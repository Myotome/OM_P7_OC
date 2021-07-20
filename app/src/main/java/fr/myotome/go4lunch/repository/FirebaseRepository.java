package fr.myotome.go4lunch.repository;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.myotome.go4lunch.model.FavoriteRestaurant;
import fr.myotome.go4lunch.model.User;

public class FirebaseRepository {
    private final Application mApplication;
    private static final String COLLECTION = "Workmates";
    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private final CollectionReference mUsersRef = rootRef.collection(COLLECTION);
    private final MutableLiveData<User> mUserMutableLiveData = new MutableLiveData<>();

    public FirebaseRepository(Application application) {
        mApplication = application;
    }

    //----------Firebase-------------//
    public void signInWithFirebaseCredential(AuthCredential authCredential) {
        mFirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this::singInBody);
    }

    public void signInWithEmailAndPassword(String email, String password) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                singInBody(task);
            }else {
                Toast.makeText(mApplication, "Something wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createUserWithEmailAndPassword(String name, String email, String password) {

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        User user = new User(uId, name, email, "https://gravatar.com/avatar/f91aabd46a5c3abef7fe229434346b38?s=400&d=identicon&r=x");
                        createUserInFirestore(user);
                        mUserMutableLiveData.setValue(user);
                    } else {
                        Toast.makeText(mApplication, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void singInBody(Task<AuthResult> task) {
        if (task.isSuccessful()) {

            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                String urlPicture;
                String uid = firebaseUser.getUid();
                String name = firebaseUser.getDisplayName();
                String email1 = firebaseUser.getEmail();
                if (firebaseUser.getPhotoUrl() != null) {
                    urlPicture = firebaseUser.getPhotoUrl().toString();
                } else {
                    urlPicture = "https://gravatar.com/avatar/f91aabd46a5c3abef7fe229434346b38?s=400&d=identicon&r=x";
                }
                User user = new User(uid, name, email1, urlPicture);

                createUserInFirestore(user);
                mUserMutableLiveData.setValue(user);
            }
        } else {
            Toast.makeText(mApplication, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setNullUser(){
        mUserMutableLiveData.setValue(new User("","","",""));
    }

    public LiveData<User> getUserMutableLiveData() {
        return mUserMutableLiveData;
    }

    //-----------Firestore-------------//
    public void createUserInFirestore(User user) {

        DocumentReference uidRef = mUsersRef.document(user.getUid());
        uidRef.get().addOnCompleteListener(uidTask -> {
            if (uidTask.isSuccessful()) {
                DocumentSnapshot document = uidTask.getResult();
                if (!document.exists()) {
                    uidRef.set(user);
                }
            } else {
                Toast.makeText(mApplication, Objects.requireNonNull(uidTask.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> fireStoreAllUsers = new MutableLiveData<>();
        mUsersRef.addSnapshotListener(((value, error) -> {
            if (error != null) {
                Toast.makeText(mApplication, error.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                List<User> users = new ArrayList<>();
                assert value != null;
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    users.add(documentSnapshot.toObject(User.class));
                }
                fireStoreAllUsers.setValue(users);

            }
        }));

        return fireStoreAllUsers;
    }

    public LiveData<User> getCurrentUser() {
        MutableLiveData<User> currentUser = new MutableLiveData<>();
        if(mFirebaseAuth.getUid() != null) {
            DocumentReference uidRef = mUsersRef.document(mFirebaseAuth.getUid());
            uidRef.addSnapshotListener((value, error) -> {
                if (error != null) {
                    Toast.makeText(mApplication, error.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (value != null) {
                        currentUser.setValue(value.toObject(User.class));
                    }
                }
            });
        }else {
            currentUser.setValue(new User("","","",""));
        }
        return currentUser;
    }

    public void setRestaurantForLunch(String placeId, String name) {
        mUsersRef.document(Objects.requireNonNull(mFirebaseAuth.getUid())).update("restaurantPlaceId", placeId);
        mUsersRef.document(mFirebaseAuth.getUid()).update("restaurantName", name);
    }

    public void setFavoriteRestaurantForCurrentUser(FavoriteRestaurant favoriteRestaurant) {
        mUsersRef.document(Objects.requireNonNull(mFirebaseAuth.getUid()))
                .collection("favorite_restaurant")
                .document(favoriteRestaurant.getPlaceId())
                .set(favoriteRestaurant);
    }

    public void deleteFavoriteRestaurantForCurrentUser(FavoriteRestaurant favoriteRestaurant) {

        mUsersRef.document(Objects.requireNonNull(mFirebaseAuth.getUid()))
                .collection("favorite_restaurant")
                .document(favoriteRestaurant.getPlaceId())
                .delete();

    }

    public LiveData<List<FavoriteRestaurant>> getAllFavoritesRestaurantsForCurrentUser() {
        MutableLiveData<List<FavoriteRestaurant>> listOfFavoriteRestaurant = new MutableLiveData<>();
        DocumentReference uidRef = mUsersRef.document(Objects.requireNonNull(mFirebaseAuth.getUid()));
        uidRef.collection("favorite_restaurant")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(mApplication, error.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        if (value != null) {
                            listOfFavoriteRestaurant.setValue(value.toObjects(FavoriteRestaurant.class));
                        }
                    }
                });

        return listOfFavoriteRestaurant;
    }


}
