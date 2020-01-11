package com.example.samuel.pentrufacultate.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.activities.MainActivity;
import com.example.samuel.pentrufacultate.adapters.RetailStoreListAdapter;
import com.example.samuel.pentrufacultate.managers.DataManager;
import com.example.samuel.pentrufacultate.models.ShoppingItem;
import com.example.samuel.pentrufacultate.models.ShoppingList;
import com.example.samuel.pentrufacultate.models.StringHelper;
import com.example.samuel.pentrufacultate.products.apis.ProductsApi;
import com.example.samuel.pentrufacultate.products.models.CatalogProduct;
import com.example.samuel.pentrufacultate.products.models.get_stores_for_products.RetailStore;
import com.example.samuel.pentrufacultate.products.models.get_stores_for_products.RetailStores;
import com.example.samuel.pentrufacultate.products.storage.DatabaseHelper;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class CheckShoppingList extends Fragment implements View.OnClickListener, Callback<RetailStores> {
    private final String TAG = StringHelper.getTag(MainActivity.class, CheckShoppingList.class);
    private TextView tvProgressLabel;
    private Button searchStoresBtn;
    SeekBar seekBar;
    DataManager mDataManager;
    ShoppingList mCurrentShoppingList;
    DatabaseHelper mDataBaseHelper;
    LinearLayout resultDisplayLayout;
    ArrayList<RetailStore> storesSearchResult;
    ArrayList<RetailStore> resultDisplayAdapterList;
    ArrayAdapter<String> arrayAdapter;

    RecyclerView mRecyclerView;
    private RetailStoreListAdapter mAdapter;

    private int MAX_RESULT_COUNT = 7;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mDataManager = DataManager.getInstance(getContext());
        mCurrentShoppingList = mDataManager.getCurrentShoppingList();
        mDataBaseHelper = new DatabaseHelper(getContext());
        storesSearchResult = new ArrayList<>();
        resultDisplayAdapterList = new ArrayList<>();
        return inflater.inflate(R.layout.fragment_check_the_shopping_list_layout, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        seekBar = view.findViewById(R.id.seekBar);
        searchStoresBtn = view.findViewById(R.id.search_stores_btn);
        searchStoresBtn.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        resultDisplayLayout = view.findViewById(R.id.stores_search_result);
        mRecyclerView=view.findViewById(R.id.recyclerView);
//        arrayAdapter = new ArrayAdapter<String>(getContext(),
//                android.R.layout.simple_list_item_1,
//                ((List<String>) resultDisplayAdapterList));

        int progress = seekBar.getProgress();
        tvProgressLabel = view.findViewById(R.id.distance_selected_view);
        float progressAsFloat = ((float) progress / 100);
        tvProgressLabel.setText("Căutare în jurul locației curente pe o rază de " + progressAsFloat + " km");
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            Log.i("FLOAT_TEST", "onProgressChanged: " + Float.toString(((float) (progress / 100))));
            float progressAsFloat = ((float) progress / 100);
            if ((progressAsFloat - ((int) progressAsFloat)) == 0) {
                tvProgressLabel.setText("Căutare în jurul locației curente pe o rază de " + (int) progressAsFloat + " km");
            } else {
                tvProgressLabel.setText("Căutare în jurul locației curente pe o rază de " + progressAsFloat + " km");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_stores_btn:
                getStoresForShoppingListByLocation("47.1725036", "27.547524", String.valueOf(seekBar.getProgress() * 10), parseShoppingListForSearch(), "price");
                break;
        }
    }

    @Override
    public void onResponse(Call<RetailStores> call, Response<RetailStores> response) {

        if (response.isSuccessful()) {
            RetailStores retailStores;

            int tempMaxResultCount = MAX_RESULT_COUNT;
            if (MAX_RESULT_COUNT > response.body().getItems().getRetailStoreList().size()) {
                tempMaxResultCount = response.body().getItems().getRetailStoreList().size();
            }
            for (int i = 0; i < tempMaxResultCount; i++) {
                resultDisplayAdapterList.add(response.body().getItems().getRetailStoreList().get(i));
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter = new RetailStoreListAdapter(getContext(), resultDisplayAdapterList);
                    mRecyclerView.setAdapter(mAdapter);
                    RecyclerView.LayoutManager layoutManager =
                            new LinearLayoutManager(getContext());
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setHasFixedSize(true);
                }
            });
            Log.i(TAG, "onResponse: with succes");
        } else {
            Log.i(TAG, "onResponse: with error");
        }
    }

    @Override
    public void onFailure(Call<RetailStores> call, Throwable t) {
        Log.i(TAG, "onFailure: ");
    }

    private void getStoresForShoppingListByLocation(String latitude, String longitude, String distanceInMeters, String productsDividedByComma, String orderBy) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.monitorulpreturilor.info/").addConverterFactory(SimpleXmlConverterFactory.create())
                        .build();

                ProductsApi rssapi = retrofit.create(ProductsApi.class);

                Call<RetailStores> call = rssapi.getStoresForProductsByLatLon(latitude, longitude, distanceInMeters, productsDividedByComma, orderBy);
                call.enqueue(CheckShoppingList.this);
            }
        }).start();

    }

    public String parseShoppingListForSearch() {
        StringBuilder result = new StringBuilder();
        ShoppingItem shoppingItem;
        CatalogProduct catalogProduct;
        for (int i = 0; i < mCurrentShoppingList.getSize(); i++) {
            shoppingItem = mCurrentShoppingList.getItemFromPosition(i);
            catalogProduct = mDataBaseHelper.getProductByName(shoppingItem.getName());
            if (!catalogProduct.getId().equals("-1")) {
                if (i < mCurrentShoppingList.getSize() - 1) {
                    result.append(catalogProduct.getId()).append(",");
                } else {
                    result.append(catalogProduct.getId());
                }
            }
        }
        return result.toString();
    }

}
