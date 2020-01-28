package com.example.samuel.pentrufacultate.products.clients;

import android.content.Context;
import android.util.Log;

import com.example.samuel.pentrufacultate.products.apis.ProductsAPIs;
import com.example.samuel.pentrufacultate.products.models.get_stores_for_products.RetailStores;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class GetStoresForProductsCallClient implements Callback<RetailStores> {
    private Context bgContext;
    private final String TAG= GetStoresForProductsCallClient.class.getSimpleName();

    public void run(Context context) {
        bgContext = context;
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.monitorulpreturilor.info/").addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        ProductsAPIs rssapi = retrofit.create(ProductsAPIs.class);

        Call<RetailStores> call = rssapi.getStoresForProductsByLatLon("47.1725036", "27.547524", "3700", "207635,202291,200299,208242,219309", "price");
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<RetailStores> call, Response<RetailStores> response) {

        if (response.isSuccessful()) {
            Log.i(TAG, "onResponse: with succes");
        } else {
            Log.i(TAG, "onResponse: with error");
        }
    }

    @Override
    public void onFailure(Call<RetailStores> call, Throwable t) {
        Log.i(TAG, "onFailure: ");
    }

}
