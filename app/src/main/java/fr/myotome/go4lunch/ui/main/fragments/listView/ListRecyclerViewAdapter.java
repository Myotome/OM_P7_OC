package fr.myotome.go4lunch.ui.main.fragments.listView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import fr.myotome.go4lunch.BuildConfig;
import fr.myotome.go4lunch.databinding.FrameListviewBinding;
import fr.myotome.go4lunch.ui.detailed.DetailRestaurantActivity;

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.RecyclerViewHolder> implements Filterable {

    private FrameListviewBinding mBinding;
    private List<ItemListViewState> mData;
    private List<ItemListViewState> mFullData;


    @NonNull
    @NotNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mBinding = FrameListviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecyclerViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerViewHolder holder, int position) {
        ItemListViewState currentData = mFullData.get(position);
        holder.mTitle.setText(currentData.getRestaurantName());
        holder.mAddress.setText(currentData.getRestaurantAddress());
        holder.mOpening.setText(currentData.getRestaurantOpening());
        holder.mDistance.setText(currentData.getDistance());
        holder.mWorkmate.setText(currentData.getHowManyWorkmate());
        holder.mRatingBar.setRating(currentData.getRestaurantRating());

        Glide.with(holder.mTitle.getContext())
                .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=50&photoreference="
                            + currentData.getRestaurantPictureUrl()
                            + "&key=" + BuildConfig.MAPS_API_KEY)

                    .into(holder.mPhoto);

        holder.mConstraintLayout.setOnClickListener(v -> {
            Intent intent = new Intent(holder.mTitle.getContext(), DetailRestaurantActivity.class);
            intent.putExtra("restaurant", currentData.getPlaceId());
            holder.mTitle.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public Filter getFilter() {
        return resultFilteredList;
    }

    private final Filter resultFilteredList = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ItemListViewState> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mFullData);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ItemListViewState item : mFullData) {
                    if (item.getRestaurantName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mData.clear();
            mData.addAll((List) results.values);
            notifyDataSetChanged();

        }
    };


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTitle = mBinding.tvListviewTitle;
        private final TextView mAddress = mBinding.tvListviewAddress;
        private final TextView mOpening = mBinding.tvListviewOpening;
        private final TextView mDistance = mBinding.tvListviewDistance;
        private final TextView mWorkmate = mBinding.tvListviewWorkmate;
        private final ImageView mPhoto = mBinding.ivListviewPicture;
        private final RatingBar mRatingBar = mBinding.rbListview;
        private final ConstraintLayout mConstraintLayout = mBinding.clListviewRestaurant;

        public RecyclerViewHolder(@NonNull FrameListviewBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    public void setData(List<ItemListViewState> data) {
        mData = data;
        mFullData = new ArrayList<>(data);
        notifyDataSetChanged();
    }

}