package fr.myotome.go4lunch.ui.authentication;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.Arrays;

import fr.myotome.go4lunch.BuildConfig;
import fr.myotome.go4lunch.R;
import fr.myotome.go4lunch.databinding.ActivityAuthBinding;
import fr.myotome.go4lunch.databinding.MailConnectionInterfaceBinding;
import fr.myotome.go4lunch.databinding.MailRegistrationInterfaceBinding;
import fr.myotome.go4lunch.ui.main.MainActivity;
import fr.myotome.go4lunch.ui.utils.ViewModelFactory;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding mActivityAuthBinding;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient mGoogleSignInClient;
    private AuthViewModel mAuthViewModel;
    private CallbackManager mCallbackManager;
    private static final int RC_SIGN_IN = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Need to be first, before root view
        initTwitterConnexionRequest();

        mActivityAuthBinding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(mActivityAuthBinding.getRoot());

        mCallbackManager = CallbackManager.Factory.create();
        initFacebookConnexionRequest();
        initAuthViewModel();
        getPermissions();
        createGoogleConnexionRequest();
        twitterSignIn();


        mActivityAuthBinding.btAuthGoogle.setOnClickListener(v -> googleSignIn());
        mActivityAuthBinding.btAuthMail.setOnClickListener(v -> showAlertDialogMailConnection());
        mActivityAuthBinding.btAuthFacebook.setOnClickListener(v ->
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile")));


    }

    private void getPermissions() {

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)
                && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET,
                    Manifest.permission.CALL_PHONE};
            ActivityCompat.requestPermissions(this, permissions, 2);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.warning);
            builder.setMessage(R.string.message);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.parameters, (dialog, which) -> {
                final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                final Uri uri = Uri.fromParts("package", AuthActivity.this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                finish();
            });
            builder.create().show();
        }
    }

    private void startToNextActivity() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initAuthViewModel() {
        mAuthViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(AuthViewModel.class);
        mAuthViewModel.getSignInMutableLiveData().observe(this, user -> {
            if (!user.getUid().equals("")) {
                startToNextActivity();
            }
        });
    }

    private void createGoogleConnexionRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void initFacebookConnexionRequest() {
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                signInWithAuthCredential(credential);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    private void initTwitterConnexionRequest() {
        TwitterAuthConfig config = new TwitterAuthConfig(BuildConfig.Api_Key, BuildConfig.Api_Secret);
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(config)
                .build();
        Twitter.initialize(twitterConfig);
    }

    private void twitterSignIn() {

        mActivityAuthBinding.btAuthTwitter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                AuthCredential credential = TwitterAuthProvider.getCredential(result.data.getAuthToken().token,
                        result.data.getAuthToken().secret);
                signInWithAuthCredential(credential);
            }

            @Override
            public void failure(TwitterException exception) {
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                // Signed in successfully, show authenticated UI.
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuthViewModel.signInWithCredential(credential);
            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        mActivityAuthBinding.btAuthTwitter.onActivityResult(requestCode, resultCode, data);
    }

    private void signInWithAuthCredential(AuthCredential credential) {
        mAuthViewModel.signInWithCredential(credential);
    }

    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getUid().length() > 2) {
            startToNextActivity();
        }
    }

    public void showAlertDialogMailConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        MailConnectionInterfaceBinding mciBinding = MailConnectionInterfaceBinding.inflate(LayoutInflater.from(this));
        builder.setView(mciBinding.getRoot());

        mciBinding.btConnectionMailValidate.setOnClickListener(v -> {

            String email = mciBinding.etConnectionMailEmail.getText().toString().trim();
            String password = mciBinding.etConnectionMailPassword.getText().toString().trim();

            if (email.isEmpty()) {
                mciBinding.etConnectionMailEmail.setError(getText(R.string.request_mail));
                mciBinding.etConnectionMailEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mciBinding.etConnectionMailEmail.setError(getText(R.string.request_mail_format));
                mciBinding.etConnectionMailEmail.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                mciBinding.etConnectionMailPassword.setError(getText(R.string.request_password));
                mciBinding.etConnectionMailPassword.requestFocus();
                return;
            }
            if (password.length() < 6) {
                mciBinding.etConnectionMailPassword.setError(getText(R.string.request_long_password));
                mciBinding.etConnectionMailPassword.requestFocus();
                return;
            }

            mAuthViewModel.signInWithEmailAndPassword(email, password);
        });

        mciBinding.btConnectionMailForgot.setOnClickListener(v -> {
            String email = mciBinding.etConnectionMailEmail.getText().toString().trim();

            if (email.isEmpty()) {
                mciBinding.etConnectionMailEmail.setError(getText(R.string.request_mail));
                mciBinding.etConnectionMailEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mciBinding.etConnectionMailEmail.setError(getText(R.string.request_mail_format));
                mciBinding.etConnectionMailEmail.requestFocus();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task ->
                            Toast.makeText(AuthActivity.this, R.string.change_password, Toast.LENGTH_SHORT).show());
        });

        mciBinding.btConnectionMailNew.setOnClickListener(v -> showAlertDialogMailRegistration());

        builder.create().show();
    }

    public void showAlertDialogMailRegistration() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        MailRegistrationInterfaceBinding mriBinding = MailRegistrationInterfaceBinding.inflate(LayoutInflater.from(this));
        builder.setView(mriBinding.getRoot());
        builder.setNegativeButton(R.string.back, (dialog, which) -> {

        });


        mriBinding.btRegistrationMailValidate.setOnClickListener(v -> {
            String firstName = mriBinding.etRegistrationMailFirstname.getText().toString().trim();
            String email = mriBinding.etRegistrationMailEmail.getText().toString().trim();
            String password = mriBinding.etRegistrationMailPassword.getText().toString().trim();

            if (firstName.isEmpty()) {
                mriBinding.etRegistrationMailFirstname.setError(getText(R.string.request_name));
                mriBinding.etRegistrationMailFirstname.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                mriBinding.etRegistrationMailEmail.setError(getText(R.string.request_mail));
                mriBinding.etRegistrationMailEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mriBinding.etRegistrationMailEmail.setError(getText(R.string.request_mail_format));
                mriBinding.etRegistrationMailEmail.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                mriBinding.etRegistrationMailPassword.setError(getText(R.string.request_password));
                mriBinding.etRegistrationMailPassword.requestFocus();
                return;
            }
            if (password.length() < 6) {
                mriBinding.etRegistrationMailPassword.setError(getText(R.string.request_long_password));
                mriBinding.etRegistrationMailPassword.requestFocus();
                return;
            }

            mAuthViewModel.createUserWithEmail(firstName, email, password);
        });

        builder.create().show();

    }


}