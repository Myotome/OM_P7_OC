package fr.myotome.go4lunch.model;

import androidx.annotation.Nullable;
import java.io.Serializable;

public class User implements Serializable {
    private String mUid;
    private String mMail;
    private String mFirstName;
    @Nullable
    private String mUrlPicture;
    private String mRestaurantPlaceId;
    private String mRestaurantName;

    //Empty constructor needed for fireStore
    public User(){}

    // TODO MYOTOME très confusant le constructeur avec uniquement 4 des 6 champs persistés sur Firestore :p
    public User(String uid, String firstName, String mail, @Nullable String urlPicture) {
        mUid = uid;
        mFirstName = firstName;
        mMail = mail;
        mUrlPicture = urlPicture;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getMail() {
        return mMail;
    }

    public void setMail(String mail) {
        mMail = mail;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    @Nullable
    public String getUrlPicture() {
        return mUrlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        mUrlPicture = urlPicture;
    }


    public String getRestaurantPlaceId() {
        return mRestaurantPlaceId;
    }

    public void setRestaurantPlaceId(String restaurantPlaceId) {
        mRestaurantPlaceId = restaurantPlaceId;
    }

    public String getRestaurantName() {
        return mRestaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        mRestaurantName = restaurantName;
    }
}
