package fr.myotome.go4lunch.ui.main.fragments.workmates;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import fr.myotome.go4lunch.databinding.FrameWorkmatesBinding;
import fr.myotome.go4lunch.ui.detailed.DetailRestaurantActivity;

public class WorkmatesRecyclerViewAdapter extends RecyclerView.Adapter<WorkmatesRecyclerViewAdapter.RecyclerViewHolder> {

    private FrameWorkmatesBinding mBinding;
    private List<WorkmatesViewState> mData = new ArrayList<>();


    @NonNull
    @NotNull
    @Override
    public WorkmatesRecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mBinding = FrameWorkmatesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecyclerViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerViewHolder holder, int position) {
        WorkmatesViewState viewState = mData.get(position);

        Glide.with(holder.mAvatar.getContext())
                .load(viewState.getUrlPicture())
                .circleCrop()
                .into(holder.mAvatar);

        holder.mName.setText(viewState.getName());
        holder.mRestaurantName.setText(viewState.getRestaurantName());

        if (!viewState.getPlaceId().equals("")) {
            holder.mConstraintLayout.setOnClickListener(v -> {
                Intent intent = new Intent(holder.mName.getContext(), DetailRestaurantActivity.class);
                intent.putExtra("restaurant", viewState.getPlaceId());
                holder.mName.getContext().startActivity(intent);
            });
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mAvatar = mBinding.ivFrameWorkmateAvatar;
        private final TextView mName = mBinding.tvFrameWorkmatesName;
        private final TextView mRestaurantName = mBinding.tvFrameWorkmatesRestaurant;
        private final ConstraintLayout mConstraintLayout = mBinding.clFrameWorkmate;

        public RecyclerViewHolder(@NonNull @NotNull FrameWorkmatesBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    public void setData(List<WorkmatesViewState> data) {
        mData = data;
        notifyDataSetChanged();
    }

}
