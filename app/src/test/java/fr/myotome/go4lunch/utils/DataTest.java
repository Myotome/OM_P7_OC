package fr.myotome.go4lunch.utils;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import fr.myotome.go4lunch.model.FavoriteRestaurant;
import fr.myotome.go4lunch.model.User;
import fr.myotome.go4lunch.model.autocompletePOJO.AutocompletePojo;
import fr.myotome.go4lunch.model.nearbySearchPOJO.Geometry;
import fr.myotome.go4lunch.model.nearbySearchPOJO.NearbySearchPojo;
import fr.myotome.go4lunch.model.nearbySearchPOJO.Result;
import fr.myotome.go4lunch.ui.main.fragments.listView.ItemListViewState;

public class DataTest {

    private static final String sRestaurantNameTest = "Restaurant name test";
    private static final String sRestaurantAddressTest = "Restaurant address test";
    private static final boolean sRestaurantOpeningTest = true;
//    private static final String sDistanceTest = "Distance test";
//    private static final String sHowManyWorkmateTest = "Coworker number test";
//    private static final String sRestaurantPictureUrlTest = "www.picture_of_restaurant.test";
    private static final double sRestaurantRatingTest = 3;
    private static final String sPlaceIdTest = "123456789azertyuiop";
    private static final String sPhoneNumberTest = "0123456789";
    private static final String sRestaurantWebsiteTest = "www.restaurant.com";

    public static User generateUserTest() {
        User user = new User("123456", "User Test", "user.test@test.com", "www.avatar.com");
        user.setRestaurantPlaceId("951357");
        user.setRestaurantName("Current user restaurant choice");
        return user;
    }

//    public static String generateDescriptionTest() {
//        return "Description from Auto complete POJO";
//    }

    public static double generateLatitudeFromNearbySearchTest() {
        return 46.578361;
    }

    public static double generateLongitudeFromNearbySearchTest() {
        return 0.339655;
    }

    public static String generateRestaurantPlaceIdTest() {
        return sPlaceIdTest;
    }

    public static com.google.android.gms.maps.model.LatLng generateCurrentPositionTest() {
        return new LatLng(46.580277, 0.339898);
    }

    public static List<User> generateCoworkerTest() {
        List<User> fakeCoworker = new ArrayList<>();

        User harry = new User("10", "Harry", "Harry's mail", "null");
        harry.setRestaurantName("Rivendell restaurant");
        harry.setRestaurantPlaceId("123456789azertyuiop");
        fakeCoworker.add(harry);

        User hermione = new User("11", "Hermione", "Hermione's mail", "null");
        hermione.setRestaurantName("Hermione's restaurant");
        hermione.setRestaurantPlaceId("987654");
        fakeCoworker.add(hermione);

        User ron = new User("12", "Ron", "Ron's mail", "null");
        ron.setRestaurantName("Ron's restaurant");
        ron.setRestaurantPlaceId("962154");
        fakeCoworker.add(ron);

        return fakeCoworker;
    }

    public static String getRestaurantNameTest() {
        return sRestaurantNameTest;
    }

    public static String getRestaurantAddressTest() {
        return sRestaurantAddressTest;
    }

    public static boolean getRestaurantOpeningTest() {
        return sRestaurantOpeningTest;
    }

//    public static String getDistanceTest() {
//        return sDistanceTest;
//    }
//
//    public static String getHowManyWorkmateTest() {
//        return sHowManyWorkmateTest;
//    }
//
//    public static String getRestaurantPictureUrlTest() {
//        return sRestaurantPictureUrlTest;
//    }

    public static double getRestaurantRatingTest() {
        return sRestaurantRatingTest;
    }

    public static String getRestaurantWebsiteTest() {
        return sRestaurantWebsiteTest;
    }

    public static String getPhoneNumberTest() {
        return sPhoneNumberTest;
    }

    public static List<FavoriteRestaurant> getFavoriteRestaurantTest() {
        List<FavoriteRestaurant> favoriteRestaurantList = new ArrayList<>();
        favoriteRestaurantList.add(new FavoriteRestaurant("123456789azertyuiop", "Rivendell restaurant", "www.rivendell.picture"));
        favoriteRestaurantList.add(new FavoriteRestaurant("2", "Moria restaurant", "www.moria.picture"));
        favoriteRestaurantList.add(new FavoriteRestaurant("3", "Mordor restaurant", "www.mordor.picture"));
        favoriteRestaurantList.add(new FavoriteRestaurant("4", "Shire restaurant", "www.shire.picture"));
        favoriteRestaurantList.add(new FavoriteRestaurant("5", "Isengard restaurant", "www.isengard.picture"));

        return favoriteRestaurantList;
    }
}
