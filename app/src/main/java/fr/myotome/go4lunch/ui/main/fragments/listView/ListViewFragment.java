package fr.myotome.go4lunch.ui.main.fragments.listView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import fr.myotome.go4lunch.databinding.FragmentListViewBinding;
import fr.myotome.go4lunch.ui.utils.ViewModelFactory;

public class ListViewFragment extends Fragment {

    private ListRecyclerViewAdapter mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fr.myotome.go4lunch.databinding.FragmentListViewBinding listViewBinding = FragmentListViewBinding.inflate(inflater, container, false);
        mRecyclerView = new ListRecyclerViewAdapter();
        listViewBinding.rvFragListview.setHasFixedSize(true);
        listViewBinding.rvFragListview.setLayoutManager(new LinearLayoutManager(listViewBinding.getRoot().getContext()));
        listViewBinding.rvFragListview.setAdapter(mRecyclerView);

        ListViewModel viewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(ListViewModel.class);
        viewModel.getMediatorLiveData().observe(getViewLifecycleOwner(), listViewState ->
                mRecyclerView.setData(listViewState.getItemListViewState()));

        return listViewBinding.getRoot();
    }
}
