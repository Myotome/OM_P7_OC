package fr.myotome.go4lunch.ui.main;

public class MainViewState {
    private final String mUserName;
    private final String mUrlPicture;
    private final String mUserMail;
    private final String mUserChoicePlaceId;

    public MainViewState(String userName, String urlPicture, String userMail, String userChoicePlaceId) {
        mUserName = userName;
        mUrlPicture = urlPicture;
        mUserMail = userMail;
        mUserChoicePlaceId = userChoicePlaceId;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getUrlPicture() {
        return mUrlPicture;
    }

    public String getUserMail() {
        return mUserMail;
    }

    public String getUserChoicePlaceId() {
        return mUserChoicePlaceId;
    }
}
