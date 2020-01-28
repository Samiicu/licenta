package com.example.samuel.pentrufacultate.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.activities.MainActivity;
import com.example.samuel.pentrufacultate.adapters.AdapterForDisplayStores;
import com.example.samuel.pentrufacultate.managers.DataManager;
import com.example.samuel.pentrufacultate.managers.GpsTrackerManager;
import com.example.samuel.pentrufacultate.models.ShoppingItem;
import com.example.samuel.pentrufacultate.models.ShoppingList;
import com.example.samuel.pentrufacultate.models.StringHelper;
import com.example.samuel.pentrufacultate.products.apis.ProductsAPIs;
import com.example.samuel.pentrufacultate.products.models.CatalogProduct;
import com.example.samuel.pentrufacultate.products.models.get_stores_for_products.RetailStore;
import com.example.samuel.pentrufacultate.products.models.get_stores_for_products.RetailStores;
import com.example.samuel.pentrufacultate.products.storage.DatabaseHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class CheckShoppingListFragment extends Fragment implements View.OnClickListener, Callback<RetailStores> {
    private static final int PERMISSION_ID = 11;
    private final String TAG = StringHelper.getTag(MainActivity.class, CheckShoppingListFragment.class);
    private TextView tvProgressLabel;
    private Button searchStoresBtn;
    private SeekBar seekBar;
    private DataManager mDataManager;
    private ShoppingList mCurrentShoppingList;
    private DatabaseHelper mDataBaseHelper;
    private LinearLayout resultDisplayLayout;
    private ArrayList<RetailStore> storesSearchResult;
    private ArrayList<RetailStore> resultDisplayAdapterList;
    private LocationManager lm;
    Location mLastLocation;
    FusedLocationProviderClient mFusedLocationClient;
    RecyclerView mRecyclerView;
    private AdapterForDisplayStores mAdapter;
    // GPSTracker class
    GpsTrackerManager gps;


    private int MAX_RESULT_COUNT = 7;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mDataManager = DataManager.getInstance(getContext());
        mCurrentShoppingList = mDataManager.getCurrentShoppingList();
        mDataBaseHelper = new DatabaseHelper(getContext());
        storesSearchResult = new ArrayList<>();
        resultDisplayAdapterList = new ArrayList<>();
        mContext = getContext();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
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
        mRecyclerView = view.findViewById(R.id.recyclerView);
        int progress = seekBar.getProgress();
        tvProgressLabel = view.findViewById(R.id.distance_selected_view);
        float progressAsFloat = ((float) progress / 100);
        tvProgressLabel.setText("Căutare în jurul locației curente pe o rază de " + progressAsFloat + " km");

        lm = (LocationManager) view.getContext().getSystemService(Context.LOCATION_SERVICE);
        setRetainInstance(true);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

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
                // create class object
                gps = new GpsTrackerManager(mContext);

                // check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line
                    Toast.makeText(mContext.getApplicationContext(), "Your Location is - \nLat: "
                            + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    getStoresForShoppingListByLocation(String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), String.valueOf(seekBar.getProgress() * 10), parseShoppingListForSearch(), "price");
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showLocationNoEnabledAlert();
                }

                break;
        }
    }


    @Override
    public void onResponse(Call<RetailStores> call, Response<RetailStores> response) {

        if (response.isSuccessful()) {
            RetailStores retailStores;

            int tempResultCount = response.body().getItems().getRetailStoreList().size();
            resultDisplayAdapterList = new ArrayList<>();
            for (int i = 0; i < tempResultCount; i++) {
                resultDisplayAdapterList.add(response.body().getItems().getRetailStoreList().get(i));
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.removeAllViews();
                    mAdapter = new AdapterForDisplayStores(getContext(), resultDisplayAdapterList);
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

                ProductsAPIs rssapi = retrofit.create(ProductsAPIs.class);

                Call<RetailStores> call = rssapi.getStoresForProductsByLatLon(latitude, longitude, distanceInMeters, productsDividedByComma, orderBy);
                call.enqueue(CheckShoppingListFragment.this);
            }
        }).start();

    }

    private String parseShoppingListForSearch() {
        StringBuilder result = new StringBuilder();
        ShoppingItem shoppingItem;
        CatalogProduct catalogProduct;
        if (mCurrentShoppingList != null) {
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
        }
        return result.toString();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getActivity() != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    @Override
    public void onPause() {
        if (gps != null) {
            gps.stopUsingGPS();
        }
        super.onPause();
    }
}
