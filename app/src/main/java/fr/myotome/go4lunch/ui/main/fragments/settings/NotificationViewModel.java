package fr.myotome.go4lunch.ui.main.fragments.settings;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.myotome.go4lunch.R;
import fr.myotome.go4lunch.model.User;
import fr.myotome.go4lunch.repository.FirebaseRepository;

public class NotificationViewModel extends AndroidViewModel {

    private final WorkManager mWorkManager;
    private static final String NOTIFICATION = "notification";
    private final MediatorLiveData<NotificationViewState> mMediator = new MediatorLiveData<>();
    private final LiveData<List<WorkInfo>> mSavedWorkInfo;
    private final Application mApplication;

    private static final String TAG = "DebugKey_notification";

    public NotificationViewModel(@NonNull @NotNull Application application, FirebaseRepository firebaseRepository) {
        super(application);
        mApplication = application;

        mWorkManager = WorkManager.getInstance(application);

        LiveData<User> currentUser = firebaseRepository.getCurrentUser();
        LiveData<List<User>> getAllUsers = firebaseRepository.getAllUsers();


        mMediator.addSource(currentUser, user ->
                combine(user,
                getAllUsers.getValue()));


        mMediator.addSource(getAllUsers, userList ->
                combine(currentUser.getValue(),
                userList));

        mSavedWorkInfo = mWorkManager.getWorkInfosByTagLiveData(NOTIFICATION);


    }

    public void notificationIsChecked(boolean checked, String message) {
        if (checked) {
            sendNotification(message);
        } else {
            mWorkManager.cancelAllWorkByTag(NOTIFICATION);
        }
    }

    private void combine(@Nullable User currentUser, @Nullable List<User> allUsers) {
        if (currentUser == null) {
            return;
        }

        List<String> coworkerName = new ArrayList<>();
        if (allUsers != null) {
            for (User user : allUsers) {
                if (currentUser.getRestaurantPlaceId() != null) {
                    if (currentUser.getRestaurantPlaceId().equals(user.getRestaurantPlaceId())) {
                        coworkerName.add(user.getFirstName());
                        if (currentUser.getFirstName().equals(user.getFirstName())) {
                            coworkerName.remove(user.getFirstName());
                        }
                    }
                }
            }
        }
        if (coworkerName.isEmpty()) {
            coworkerName.add(mApplication.getResources().getString(R.string.nobody));
        }

        mMediator.setValue(new NotificationViewState(mApplication, currentUser.getFirstName(), currentUser.getRestaurantName(), coworkerName));


    }

    public LiveData<NotificationViewState> getViewState() {
        return mMediator;
    }


    /** Main method for use workManager.
     * The commented part is used to test and show the sending notification with WorkManager
     * without waiting for the time of sending
     * The log part is used show if workManager is running or not
    */
    private void sendNotification(String message) {
        PeriodicWorkRequest notificationRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.DAYS)
                .setInputData(createInputDataMessage(message))
                .addTag(NOTIFICATION)
                .setInitialDelay(timeManagementForNotification(), TimeUnit.MILLISECONDS)
                .build();

//        PeriodicWorkRequest notificationRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.MINUTES)
//                .setInputData(createInputDataMessage(message))
//                .addTag(NOTIFICATION)
//                .setInitialDelay(30000, TimeUnit.MILLISECONDS)
//                .build();


        mWorkManager.enqueueUniquePeriodicWork("WorkManagerNotification", ExistingPeriodicWorkPolicy.REPLACE, notificationRequest);

        Log.d(TAG, "sendNotification: workManager status : " + mWorkManager.getWorkInfosByTag(NOTIFICATION));

    }

    private Data createInputDataMessage(String message) {
        Data.Builder builder = new Data.Builder();
        builder.putString("325165468", message);
        return builder.build();
    }

    /** The log part is to calculate the time before the first notification
     * @return delay before notification
     */
    private long timeManagementForNotification() {
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis() ;

        if (calendar.get(Calendar.HOUR_OF_DAY) > 12 || calendar.get(Calendar.HOUR_OF_DAY) == 12 && calendar.get(Calendar.MINUTE) > 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Log.d(TAG, "timeManagementForNotification: time in millis: " + (calendar.getTimeInMillis() - currentTime));

        return (calendar.getTimeInMillis() - currentTime);
    }

    public LiveData<List<WorkInfo>> getOutputWorkInfo() {
        return mSavedWorkInfo;
    }

}
