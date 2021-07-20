package fr.myotome.go4lunch;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import fr.myotome.go4lunch.model.FavoriteRestaurant;
import fr.myotome.go4lunch.model.User;
import fr.myotome.go4lunch.model.autocompletePOJO.AutocompletePojo;
import fr.myotome.go4lunch.model.detailPlacePOJO.DetailPlacePOJO;
import fr.myotome.go4lunch.model.nearbySearchPOJO.Geometry;
import fr.myotome.go4lunch.model.nearbySearchPOJO.Location;
import fr.myotome.go4lunch.model.nearbySearchPOJO.NearbySearchPojo;
import fr.myotome.go4lunch.model.nearbySearchPOJO.Result;
import fr.myotome.go4lunch.repository.FirebaseRepository;
import fr.myotome.go4lunch.repository.MainRepository;
import fr.myotome.go4lunch.ui.detailed.DetailViewModel;
import fr.myotome.go4lunch.ui.detailed.DetailViewState;
import fr.myotome.go4lunch.ui.main.MainViewModel;
import fr.myotome.go4lunch.ui.main.MainViewState;
import fr.myotome.go4lunch.ui.main.fragments.listView.ListViewModel;
import fr.myotome.go4lunch.ui.main.fragments.listView.ListViewState;
import fr.myotome.go4lunch.ui.main.fragments.maps.MapsViewModel;
import fr.myotome.go4lunch.ui.main.fragments.maps.MapsViewState;
import fr.myotome.go4lunch.ui.main.fragments.workmates.WorkmatesViewModel;
import fr.myotome.go4lunch.ui.main.fragments.workmates.WorkmatesViewState;
import fr.myotome.go4lunch.ui.utils.MainApplication;
import fr.myotome.go4lunch.utils.DataTest;
import fr.myotome.go4lunch.utils.LiveDataTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class AllViewModelUnitTest {

    @Rule
    public final InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private final Application mApplication = MainApplication.getInstance();
    private final MainRepository mMainRepository = Mockito.mock(MainRepository.class);
    private final FirebaseRepository mFirebaseRepository = Mockito.mock(FirebaseRepository.class);

    private final MutableLiveData<User> mUserMutableLiveDataTest = new MutableLiveData<>();
    private final MutableLiveData<LatLng> mCurrentPositionMutableLiveDataTest = new MutableLiveData<>();
    private final MutableLiveData<NearbySearchPojo> mNearbySearchPojoMutableLiveDataTest = new MutableLiveData<>();
    private final MutableLiveData<AutocompletePojo> mAutocompletePojoMutableLiveDataTest = new MutableLiveData<>();
    private final MutableLiveData<List<User>> mListCoworkerMutableLiveDataTest = new MutableLiveData<>();
    private final MutableLiveData<DetailPlacePOJO> mDetailPlacePOJOMutableLiveDataTest = new MutableLiveData<>();
    private final MutableLiveData<List<FavoriteRestaurant>> mListFavoriteRestaurantMutableLiveDataTest = new MutableLiveData<>();

    private final AutocompletePojo mAutocompletePojo = Mockito.mock(AutocompletePojo.class);

    private final NearbySearchPojo mNearbySearchPojo = Mockito.mock(NearbySearchPojo.class);
    private final Result mNearbyResult = Mockito.mock(Result.class);
    private final Geometry mNearbyGeometry = Mockito.mock(Geometry.class);
    private final Location mNearbyLocation = Mockito.mock(Location.class);

    private final DetailPlacePOJO mDetailPlacePOJO = Mockito.mock(DetailPlacePOJO.class);
    private final fr.myotome.go4lunch.model.detailPlacePOJO.Result mDetailResult = Mockito.mock(fr.myotome.go4lunch.model.detailPlacePOJO.Result.class);

    private final android.location.Location mLocation = Mockito.mock(android.location.Location.class);


    /**
     * For these tests, all the results of the retrofit queries
     * are simulated in the DataTest class.
     * All repository calls are mock.
     * All POJO queries are mock.
     *
     * LiveData set values from DataTest
     */
    @Before
    public void setUp() {

        //Mock result for nearby search
        List<Result> listOfNearbyResult = new ArrayList<>();
        listOfNearbyResult.add(mNearbyResult);
        Mockito.when(mNearbySearchPojo.getResults()).thenReturn(listOfNearbyResult);
        Mockito.when(mNearbyResult.getGeometry()).thenReturn(mNearbyGeometry);
        Mockito.when(mNearbyGeometry.getLocation()).thenReturn(mNearbyLocation);
        Mockito.when(mNearbyLocation.getLat()).thenReturn(DataTest.generateLatitudeFromNearbySearchTest());
        Mockito.when(mNearbyLocation.getLng()).thenReturn(DataTest.generateLongitudeFromNearbySearchTest());
        Mockito.when(mNearbyResult.getPlaceId()).thenReturn(DataTest.generateRestaurantPlaceIdTest());
        Mockito.when(mNearbyResult.getPhotos()).thenReturn(null);
        Mockito.when(mNearbyResult.getName()).thenReturn(DataTest.getRestaurantNameTest());
        Mockito.when(mNearbyResult.getVicinity()).thenReturn(DataTest.getRestaurantAddressTest());
        Mockito.when(mNearbyResult.getRating()).thenReturn(DataTest.getRestaurantRatingTest());

        //Mock result for detail place
        Mockito.when(mDetailPlacePOJO.getResult()).thenReturn(mDetailResult);
        Mockito.when(mDetailResult.getName()).thenReturn(DataTest.getRestaurantNameTest());
        Mockito.when(mDetailResult.getPlaceId()).thenReturn(DataTest.generateRestaurantPlaceIdTest());
        Mockito.when(mDetailResult.getVicinity()).thenReturn(DataTest.getRestaurantAddressTest());
        Mockito.when(mDetailResult.getRating()).thenReturn(DataTest.getRestaurantRatingTest());
        Mockito.when(mDetailResult.getPhotos()).thenReturn(null);
        Mockito.when(mDetailResult.getFormattedPhoneNumber()).thenReturn(DataTest.getPhoneNumberTest());
        Mockito.when(mDetailResult.getWebsite()).thenReturn(DataTest.getRestaurantWebsiteTest());
        Mockito.when(mDetailResult.getOpeningHours()).thenReturn(null);

        //Use for combine methode in ViewModel, and give value

        mAutocompletePojoMutableLiveDataTest.setValue(mAutocompletePojo);
        mCurrentPositionMutableLiveDataTest.setValue(DataTest.generateCurrentPositionTest());
        mNearbySearchPojoMutableLiveDataTest.setValue(mNearbySearchPojo);
        mListCoworkerMutableLiveDataTest.setValue(DataTest.generateCoworkerTest());
        mDetailPlacePOJOMutableLiveDataTest.setValue(mDetailPlacePOJO);
        mListFavoriteRestaurantMutableLiveDataTest.setValue(DataTest.getFavoriteRestaurantTest());


        //Mock access, Mockito return when method called
        Mockito.doReturn(mUserMutableLiveDataTest).when(mFirebaseRepository).getCurrentUser();
        Mockito.when(mMainRepository.getAutocompleteQuery()).thenReturn(mAutocompletePojoMutableLiveDataTest);
        Mockito.when(mMainRepository.getNearbySearchQuery()).thenReturn(mNearbySearchPojoMutableLiveDataTest);
        Mockito.when(mMainRepository.getCurrentLocation()).thenReturn(mCurrentPositionMutableLiveDataTest);
        Mockito.when(mFirebaseRepository.getAllUsers()).thenReturn(mListCoworkerMutableLiveDataTest);
        Mockito.when(mMainRepository.getDetailPlaceQuery()).thenReturn(mDetailPlacePOJOMutableLiveDataTest);
        Mockito.when(mFirebaseRepository.getAllFavoritesRestaurantsForCurrentUser()).thenReturn(mListFavoriteRestaurantMutableLiveDataTest);
    }

    /**
     * This test verify that the ViewModel correctly combines data from LiveData.
     * If all the methods inherent in this ViewModel work, then the View State is created.
     * Assertions verify that the data passed in parameters in the LiveData,
     * are properly processed and compiled in the ViewState
     * @throws InterruptedException uses the LiveDataTestUtils class, to manage async. of LiveData
     */
    @Test
    public void shouldExposeCorrectMainViewState() throws InterruptedException {

        //Before
        mUserMutableLiveDataTest.setValue(DataTest.generateUserTest());

        //When
        MainViewModel mainViewModel = new MainViewModel(mApplication, mMainRepository, mFirebaseRepository);
        MainViewState result = LiveDataTestUtils.getOrAwaitValue(mainViewModel.getMediatorData());

        //Then
        Assert.assertEquals("User Test", result.getUserName());
        Assert.assertEquals("user.test@test.com", result.getUserMail());
        Assert.assertEquals("www.avatar.com", result.getUrlPicture());
        Assert.assertEquals("951357", result.getUserChoicePlaceId());

    }

    @Test
    public void mainViewModelShouldExposeAnotherUser() throws InterruptedException {
        //Before
        mUserMutableLiveDataTest.setValue(new User("uid", "name", "mail@mail.com", null));

        //When
        MainViewModel mainViewModel = new MainViewModel(mApplication, mMainRepository, mFirebaseRepository);
        MainViewState result = LiveDataTestUtils.getOrAwaitValue(mainViewModel.getMediatorData());

        //Then
        Assert.assertNotEquals("User Test", result.getUserName());
        Assert.assertNotEquals("user.test@test.com", result.getUserMail());
        Assert.assertNotEquals("www.avatar.com", result.getUrlPicture());
        Assert.assertNotEquals("951357", result.getUserChoicePlaceId());

        Assert.assertEquals("name", result.getUserName());
        Assert.assertEquals("mail@mail.com", result.getUserMail());
        Assert.assertEquals("", result.getUrlPicture());
        Assert.assertNull(result.getUserChoicePlaceId());
    }

    /**
     * This test verify that the ViewModel correctly combines data from LiveData.
     * If all the methods inherent in this ViewModel work, then the View State is created.
     * Assertions verify that the data passed in parameters in the LiveData,
     * are properly processed and compiled in the ViewState
     * @throws InterruptedException uses the LiveDataTestUtils class, to manage async. of LiveData
     */
    @Test
    public void shouldExposeCorrectMapsViewState() throws InterruptedException{

        //When
        MapsViewModel mapsViewModel = new MapsViewModel(mApplication, mMainRepository, mFirebaseRepository);
        MapsViewState result = LiveDataTestUtils.getOrAwaitValue(mapsViewModel.getMediatorLiveData());

        //Then
        Assert.assertEquals(46.580277, result.getCurrentLat(), 0.00001);
        Assert.assertEquals(0.339898, result.getCurrentLng(), 0.00001);
        Assert.assertEquals(46.578361, result.getListRestaurantViewStates().get(0).getRestaurantLat(), 0.00001);
        Assert.assertEquals(0.339655, result.getListRestaurantViewStates().get(0).getRestaurantLng(), 0.00001);
        Assert.assertEquals("123456789azertyuiop", result.getListRestaurantViewStates().get(0).getPlaceId());

    }

    /**
     * This test verify that the ViewModel correctly combines data from LiveData.
     * If all the methods inherent in this ViewModel work, then the View State is created.
     * Assertions verify that the data passed in parameters in the LiveData,
     * are properly processed and compiled in the ViewState
     * @throws InterruptedException uses the LiveDataTestUtils class, to manage async. of LiveData
     */
    @Test
    public void shouldExposeCorrectListViewState() throws InterruptedException {

        //Mock access, Mockito return when method called
        Mockito.doCallRealMethod().when(mLocation).setLatitude(Mockito.anyDouble());
        Mockito.doCallRealMethod().when(mLocation).setLongitude(Mockito.anyDouble());
        mLocation.setLatitude(DataTest.generateLatitudeFromNearbySearchTest());
        mLocation.setLongitude(DataTest.generateLongitudeFromNearbySearchTest());

        //When
        ListViewModel listViewModel = new ListViewModel(mApplication, mMainRepository, mFirebaseRepository);
        ListViewState result = LiveDataTestUtils.getOrAwaitValue(listViewModel.getMediatorLiveData());

        //Then
        float ratingExpected = (float) 3*3/5;
        Assert.assertNotNull(result.getItemListViewState());
        Assert.assertEquals(1, result.getItemListViewState().size());
        Assert.assertEquals("Restaurant name test", result.getItemListViewState().get(0).getRestaurantName());
        Assert.assertEquals("Restaurant address test", result.getItemListViewState().get(0).getRestaurantAddress());
        Assert.assertEquals(ratingExpected, result.getItemListViewState().get(0).getRestaurantRating(),0.1);
        Assert.assertEquals("123456789azertyuiop", result.getItemListViewState().get(0).getPlaceId());
        Assert.assertEquals("0.0 m", result.getItemListViewState().get(0).getDistance());
    }

    /**
     * This test verify that the ViewModel correctly combines data from LiveData.
     * If all the methods inherent in this ViewModel work, then the View State is created.
     * Assertions verify that the data passed in parameters in the LiveData,
     * are properly processed and compiled in the ViewState
     * @throws InterruptedException uses the LiveDataTestUtils class, to manage async. of LiveData
     */
    @Test
    public void shouldExposeCorrectWorkmatesViewState() throws InterruptedException {

        //When
        WorkmatesViewModel workmatesViewModel = new WorkmatesViewModel(mApplication, mFirebaseRepository);
        List<WorkmatesViewState> result = LiveDataTestUtils.getOrAwaitValue(workmatesViewModel.getWorkmatesList());

        //Then
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("Rivendell restaurant", result.get(0).getRestaurantName());
        Assert.assertEquals("null", result.get(0).getUrlPicture());
        Assert.assertEquals("123456789azertyuiop", result.get(0).getPlaceId());
        Assert.assertEquals("Hermione's restaurant", result.get(1).getRestaurantName());
        Assert.assertEquals("null", result.get(1).getUrlPicture());
        Assert.assertEquals("987654", result.get(1).getPlaceId());
        Assert.assertEquals("Ron's restaurant", result.get(2).getRestaurantName());
        Assert.assertEquals("null", result.get(2).getUrlPicture());
        Assert.assertEquals("962154", result.get(2).getPlaceId());

    }

    /**
     * This test verify that the ViewModel correctly combines data from LiveData.
     * If all the methods inherent in this ViewModel work, then the View State is created.
     * Assertions verify that the data passed in parameters in the LiveData,
     * are properly processed and compiled in the ViewState
     * @throws InterruptedException uses the LiveDataTestUtils class, to manage async. of LiveData
     */
    @Test
    public void shouldExposeCorrectDetailViewState() throws InterruptedException{

        //Before
        mUserMutableLiveDataTest.setValue(DataTest.generateUserTest());

        //When
        DetailViewModel detailViewModel = new DetailViewModel(mApplication, mMainRepository, mFirebaseRepository);
        DetailViewState result = LiveDataTestUtils.getOrAwaitValue(detailViewModel.getMediatorLiveData());

        //Then
        float ratingExpected = (float) 3*3/5;
        Assert.assertNotNull(result);
        Assert.assertEquals("Restaurant name test", result.getRestaurantName());
        Assert.assertEquals("Restaurant address test", result.getRestaurantAddress());
        Assert.assertEquals(ratingExpected, result.getRestaurantRating(), 0.1);
        Assert.assertEquals("", result.getUrlRestaurantPhoto());
        Assert.assertEquals("0123456789", result.getRestaurantPhoneNumber());
        Assert.assertEquals("www.restaurant.com", result.getUrlRestaurantWebsite());
        Assert.assertEquals("123456789azertyuiop", result.getPlaceId());
        Assert.assertEquals("", result.getOpeningHour());
        Assert.assertFalse(result.isFabCurrentRestaurant());
        Assert.assertEquals(1, result.getCurrentCoworker().size());
        Assert.assertEquals("Harry", result.getCurrentCoworker().get(0).getFirstName());
        Assert.assertNotEquals("Hermione", result.getCurrentCoworker().get(0).getFirstName());

    }

}
