package com.example.samuel.pentrufacultate.products.clients;

import android.content.Context;
import android.util.Log;

import com.example.samuel.pentrufacultate.products.apis.ProductsAPIs;
import com.example.samuel.pentrufacultate.products.models.CatalogProduct;
import com.example.samuel.pentrufacultate.products.models.CatalogProducts;
import com.example.samuel.pentrufacultate.products.storage.DatabaseHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * @author Anirudh Sharma
 */
@SuppressWarnings("deprecation")
public class AllProductsCallClient implements Callback<CatalogProducts> {
    Context bgContext;

    public void run(Context context) {
        bgContext=context;
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.monitorulpreturilor.info/").addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        ProductsAPIs rssapi = retrofit.create(ProductsAPIs.class);

        Call<CatalogProducts> call = rssapi.getAllProducts();
        call.enqueue(this);
    }

    public void onResponse(Call<CatalogProducts> call, Response<CatalogProducts> response) {


        if (response.isSuccessful()) {

            CatalogProducts products = response.body();
            List<CatalogProduct> productsList = products.getItems().getCatalogProductList();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DatabaseHelper mDataBase = new DatabaseHelper(bgContext);
//                    for (CatalogProduct product : productsList
//                    ) {
                        mDataBase.addBulkOfProducts(productsList);

//                        mDataBase.addProductDetail(product.getName(), product.getId());

//                    }
                }
            }).start();

//            mDataBase.getDatabaseOpenHelper().syncProducts(mDataBase.getDatabaseOpenHelper(), productsList);

            Log.i("TEST_XML", "Product from position 1: " + products.getItems());
            System.out.println("----------------------------------------------\n");


        } else {
            System.out.println(response.errorBody());
        }
    }

    public void onFailure(Call<CatalogProducts> call, Throwable t) {

        t.printStackTrace();

    }


}