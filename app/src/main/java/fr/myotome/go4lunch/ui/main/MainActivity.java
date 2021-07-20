package fr.myotome.go4lunch.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import fr.myotome.go4lunch.R;
import fr.myotome.go4lunch.databinding.ActivityMainBinding;
import fr.myotome.go4lunch.ui.authentication.AuthActivity;
import fr.myotome.go4lunch.ui.detailed.DetailRestaurantActivity;
import fr.myotome.go4lunch.ui.main.fragments.listView.ListViewFragment;
import fr.myotome.go4lunch.ui.main.fragments.maps.MapsFragment;
import fr.myotome.go4lunch.ui.main.fragments.settings.SettingsFragment;
import fr.myotome.go4lunch.ui.main.fragments.workmates.WorkmatesFragment;
import fr.myotome.go4lunch.ui.utils.ViewModelFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mActivityMainBinding;
    private MainViewModel mViewModel;
    private String mUserPlaceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mActivityMainBinding.getRoot();
        setContentView(view);

        //------------Initialize viewModel----//
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MainViewModel.class);

        mViewModel.getMediatorData().observe(this, mainViewState -> {
            mUserPlaceId = mainViewState.getUserChoicePlaceId();
            displayData(mainViewState.getUserName(), mainViewState.getUserMail(), mainViewState.getUrlPicture());

        });

        //----------Bottom nav -------------//
        mActivityMainBinding.bottomNavigation.setOnNavigationItemSelectedListener(navBottomListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new MapsFragment()).commit();

        //---------Drawer nav --------------//
        setSupportActionBar(mActivityMainBinding.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mActivityMainBinding.drawerLayout, mActivityMainBinding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mActivityMainBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mActivityMainBinding.navControllerView.setNavigationItemSelectedListener(navDrawerListener);

    }

    private void displayData(String user, String mail, String urlPicture) {

        Glide.with(this)
                .load(urlPicture)
                .circleCrop()
                .into((ImageView) mActivityMainBinding.navControllerView.getHeaderView(0).findViewById(R.id.iv_header_avatar));

        TextView email = mActivityMainBinding.navControllerView.getHeaderView(0).findViewById(R.id.tv_header_mail);
        email.setText(mail);
        TextView name = mActivityMainBinding.navControllerView.getHeaderView(0).findViewById(R.id.tv_header_name);
        name.setText(user);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getResources().getText(R.string.search_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
              mViewModel.setQuery();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()>2) {
                    mViewModel.setAutocompleteQuery(newText);
                }else{
                    mViewModel.setQuery();
                }
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            mViewModel.setQuery();
            return false;
        });

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navBottomListener = item -> {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_map:
                selectedFragment = new MapsFragment();
                break;
            case R.id.navigation_list:
                selectedFragment = new ListViewFragment();
                break;

            case R.id.navigation_workmates:
                selectedFragment = new WorkmatesFragment();
                break;
        }

        assert selectedFragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view,
                selectedFragment).commit();

        return true;

    };

    @SuppressLint("NonConstantResourceId")
    private final NavigationView.OnNavigationItemSelectedListener navDrawerListener = item -> {
        switch (item.getItemId()) {
            case R.id.drawer_nav_lunch:
                if (mUserPlaceId != null && mUserPlaceId.length()>1) {
                    Intent intent = new Intent(this, DetailRestaurantActivity.class);
                    intent.putExtra("restaurant", mUserPlaceId);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, getResources().getText(R.string.no_restaurant_chosen), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.drawer_nav_settings:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new SettingsFragment()).commit();
                break;
            case R.id.drawer_nav_logout:
                populateCloseAppDialog();
                break;
        }
        return true;
    };

    @Override
    public void onBackPressed() {
        if (mActivityMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void populateCloseAppDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.close_app_title)
                .setMessage(R.string.close_app_message)
                .setNegativeButton(R.string.no, (dialog, which) -> {
                })
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    WorkManager.getInstance(this).cancelAllWorkByTag("notification");
                    mViewModel.setNullUser();
                    startActivity( new Intent(this, AuthActivity.class));
                    Log.d("debugkey_2", "populateCloseAppDialog: ");
                    finish();
                })
                .create();

        alertDialog.show();
    }
}