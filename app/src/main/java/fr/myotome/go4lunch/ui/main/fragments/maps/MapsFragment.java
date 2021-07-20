package fr.myotome.go4lunch.ui.main.fragments.maps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Objects;

import fr.myotome.go4lunch.R;
import fr.myotome.go4lunch.databinding.FragmentMapsBinding;
import fr.myotome.go4lunch.ui.detailed.DetailRestaurantActivity;
import fr.myotome.go4lunch.ui.utils.ViewModelFactory;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MapsFragment extends Fragment {

    private SupportMapFragment mSupportMapFragment;
    private SharedPreferences mPreferences;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fr.myotome.go4lunch.databinding.FragmentMapsBinding mapsBinding = FragmentMapsBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        mPreferences = requireActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);

        initialiseViewModel();
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);



        return mapsBinding.getRoot();
    }

    private void initialiseViewModel() {
        MapsViewModel mapsViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapsViewModel.class);
        mapsViewModel.getMediatorLiveData().observe(getViewLifecycleOwner(), mapsViewState -> {
            DisplayMap(mapsViewState.getCurrentLat(), mapsViewState.getCurrentLng());
            DisplayMarker(mapsViewState.getListRestaurantViewStates());
        });
    }


    private void DisplayMap(double currentLat, double currentLng) {
        LatLng currentLocation = new LatLng(currentLat, currentLng);
        assert mSupportMapFragment != null;
        mSupportMapFragment.getMapAsync(googleMap -> {
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getApplicationContext(), mapChoice()));

                if (!success) {
                    Toast.makeText(getApplicationContext(), "Style parsing failed.", Toast.LENGTH_SHORT).show();

                }
            } catch (Resources.NotFoundException e) {
                Toast.makeText(getApplicationContext(), "Can't find style. Error: " + e, Toast.LENGTH_SHORT).show();
            }

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, mPreferences.getInt("zoom", 16)));

            //Enable auto locate button
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
        });
    }

    private void DisplayMarker(List<MapsListRestaurantViewState> listRestaurant) {

        mSupportMapFragment.getMapAsync(googleMap -> {
            googleMap.clear();

            for (MapsListRestaurantViewState data : listRestaurant) {
                LatLng restaurantPosition = new LatLng(data.getRestaurantLat(), data.getRestaurantLng());

                Marker marker = googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(data.isUserInRestaurant()))
                .position(restaurantPosition));
                assert marker != null;
                marker.setTag(data.getPlaceId());


            }

            googleMap.setOnMarkerClickListener(marker -> {
                Intent intent = new Intent(getContext(), DetailRestaurantActivity.class);
                intent.putExtra("restaurant", Objects.requireNonNull(marker.getTag()).toString());
                requireContext().startActivity(intent);
                return true;
            });

        });
    }

    private int mapChoice(){
        int index = mPreferences.getInt("map_choice", 0);
        switch (index){
            case 1 : return R.raw.custom_map_ancient;
            case 2 : return R.raw.custom_map_night;
            default: return R.raw.custom_map_classic;
        }
    }
}
