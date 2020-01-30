package com.example.samuel.pentrufacultate.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.managers.DataManager;

public class DisplayAllRecipesFragment extends Fragment {
    private static final String TAG = DisplayAllRecipesFragment.class.getSimpleName();
    //    AdapterForDisplayRecipes adapterForDisplayProcedures;
//    private ArrayList<RecipeModel> mProcedures = new ArrayList<>();
    private String uidCurrentUser;
    RecyclerView recyclerViewListOfRecipes;
    DataManager mDataManager;

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.i(TAG, "onHiddenChanged: ");
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        mDataManager = DataManager.getInstance(getContext());
        mDataManager.addListenerForDbRecipes();
        mDataManager.addListenerForShoppingListsData(getContext());


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        View view=inflater.inflate(R.layout.fragment_display_procedures, container, false);
        recyclerViewListOfRecipes = view.findViewById(R.id.display_all_procedure);
        mDataManager.addLayoutForDisplayAllRecipes(recyclerViewListOfRecipes, getContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated: ");
//        recyclerViewListOfRecipes = view.findViewById(R.id.display_all_procedure);

    }

}
