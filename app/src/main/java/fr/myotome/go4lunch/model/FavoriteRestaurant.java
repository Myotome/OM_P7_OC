package fr.myotome.go4lunch.model;

import com.google.firebase.firestore.Exclude;

public class FavoriteRestaurant {

    // TODO MYOTOME non utilisé
    private String mFirestoreDocumentId;
    private String mPlaceId;
    private String mRestaurantName;
    private String mUrlPicture;

    //Empty constructor needed for fireStore
    public FavoriteRestaurant() {
    }

    public FavoriteRestaurant(String placeId, String restaurantName, String urlPicture) {
        mPlaceId = placeId;
        mRestaurantName = restaurantName;
        mUrlPicture = urlPicture;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(String placeId) {
        mPlaceId = placeId;
    }

    public String getRestaurantName() {
        return mRestaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        mRestaurantName = restaurantName;
    }

    public String getUrlPicture() {
        return mUrlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        mUrlPicture = urlPicture;
    }

    @Exclude
    public String getFirestoreDocumentId() {
        return mFirestoreDocumentId;
    }

    public void setFirestoreDocumentId(String firestoreDocumentId) {
        mFirestoreDocumentId = firestoreDocumentId;
    }
}
