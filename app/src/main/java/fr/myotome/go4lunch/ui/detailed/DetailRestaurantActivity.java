package fr.myotome.go4lunch.ui.detailed;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;

import fr.myotome.go4lunch.BuildConfig;
import fr.myotome.go4lunch.databinding.ActivityDetailRestaurantBinding;
import fr.myotome.go4lunch.ui.utils.ViewModelFactory;

public class DetailRestaurantActivity extends AppCompatActivity {

    // TODO MYOTOME variable locale possible
    private ActivityDetailRestaurantBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDetailRestaurantBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        DetailRecyclerViewAdapter recyclerView = new DetailRecyclerViewAdapter();
        mBinding.rvDetailWorkmate.setLayoutManager(new LinearLayoutManager(mBinding.getRoot().getContext()));
        mBinding.rvDetailWorkmate.setAdapter(recyclerView);

        // TODO MYOTOME A extraire dans une constante, ou mieux, utiliser une petite méthode newInstance ou navigate
        String placeId = getIntent().getStringExtra("restaurant");
        DetailViewModel viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(DetailViewModel.class);
        viewModel.queryToDetailPlace(placeId);

        viewModel.getMediatorLiveData().observe(this, detailViewState -> {
            mBinding.tvDetailName.setText(detailViewState.getRestaurantName());
            mBinding.tvDetailAddress.setText(detailViewState.getRestaurantAddress());
            mBinding.rbDetail.setRating(detailViewState.getRestaurantRating());
            mBinding.fabDetail.setImageResource(detailViewState.getFabCurrentRestaurant());
            mBinding.tvDetailHour.setText(detailViewState.getOpeningHour());
            mBinding.btDetailLike.setCompoundDrawablesWithIntrinsicBounds(0, detailViewState.getFavoriteRestaurant(), 0, 0);

            // TODO MYOTOME URL à concaténer côté VM
            Glide.with(this)
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference="
                            + detailViewState.getUrlRestaurantPhoto()
                            + "&key=" + BuildConfig.MAPS_API_KEY)
                    .into(mBinding.ivDetailRestaurant);

            recyclerView.setData(detailViewState.getCurrentCoworker());

            mBinding.btDetailCall.setOnClickListener(v -> viewModel.callRestaurant(detailViewState.getRestaurantPhoneNumber()));

            mBinding.btDetailWebsite.setOnClickListener(v -> viewModel.openRestaurantWebsite(detailViewState.getUrlRestaurantWebsite()));

            mBinding.btDetailLike.setOnClickListener(v -> viewModel.setFavoriteRestaurant(detailViewState.getPlaceId(),
                    detailViewState.getRestaurantName(),
                    detailViewState.getUrlRestaurantPhoto(),
                    detailViewState.isFavorite()));

            mBinding.fabDetail.setOnClickListener(v -> {
                viewModel.setRestaurantForLunch(detailViewState.getPlaceId(),
                        detailViewState.getRestaurantName(),
                        detailViewState.isFabCurrentRestaurant());

                mBinding.fabDetail.setImageResource(detailViewState.getFabCurrentRestaurant());
            });


        });


    }
}