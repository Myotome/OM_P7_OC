package fr.myotome.go4lunch.ui.main.fragments.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import fr.myotome.go4lunch.databinding.FragmentWorkmatesBinding;
import fr.myotome.go4lunch.ui.utils.ViewModelFactory;

public class WorkmatesFragment extends Fragment {

    private WorkmatesRecyclerViewAdapter mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fr.myotome.go4lunch.databinding.FragmentWorkmatesBinding workmatesBinding = FragmentWorkmatesBinding.inflate(inflater, container, false);

        mRecyclerView = new WorkmatesRecyclerViewAdapter();
        workmatesBinding.rvFragWorkmate.setLayoutManager(new LinearLayoutManager(workmatesBinding.getRoot().getContext()));
        workmatesBinding.rvFragWorkmate.setAdapter(mRecyclerView);

        WorkmatesViewModel viewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(WorkmatesViewModel.class);
        viewModel.getWorkmatesList().observe(getViewLifecycleOwner(), workmatesViewStates -> mRecyclerView.setData(workmatesViewStates));

        return workmatesBinding.getRoot();
    }
}
