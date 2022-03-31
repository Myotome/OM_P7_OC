package fr.myotome.go4lunch.ui.detailed;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import fr.myotome.go4lunch.databinding.FrameWorkmatesBinding;
import fr.myotome.go4lunch.model.User;

public class DetailRecyclerViewAdapter extends RecyclerView.Adapter<DetailRecyclerViewAdapter.RecyclerViewHolder> {

    // TODO MYOTOME Erreur, chaque ViewHolder doit avoir son binding, pas l'adapter
    private FrameWorkmatesBinding mBinding;
    private List<User> mData = new ArrayList<>();

    @NonNull
    @NotNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mBinding = FrameWorkmatesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecyclerViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerViewHolder holder, int position) {
        User user = mData.get(position);

        Glide.with(holder.mAvatar.getContext())
                .load(user.getUrlPicture())
                .circleCrop()
                .into(holder.mAvatar);

        holder.mName.setText(user.getFirstName());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // TODO MYOTOME public **static** class (très important : ça évite que le ViewHolder ait une référence vers son adapter)
    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        private final ImageView mAvatar = mBinding.ivFrameWorkmateAvatar;
        private final TextView mName = mBinding.tvFrameWorkmatesName;

        public RecyclerViewHolder(@NonNull @NotNull FrameWorkmatesBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    public void setData(List<User> data){
        mData = data;
        notifyDataSetChanged();
    }


}
