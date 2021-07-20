package fr.myotome.go4lunch.ui.main.fragments.listView;

import java.util.List;

public class ListViewState {

    private final List<ItemListViewState> mItemListViewState;

    public ListViewState(List<ItemListViewState> itemListViewStates){
        mItemListViewState = itemListViewStates;
    }

    public List<ItemListViewState> getItemListViewState() {
        return mItemListViewState;
    }


}
