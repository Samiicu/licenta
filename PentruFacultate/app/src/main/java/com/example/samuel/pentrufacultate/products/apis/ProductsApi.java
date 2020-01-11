package com.example.samuel.pentrufacultate.products.apis;

import com.example.samuel.pentrufacultate.products.models.CatalogProducts;
import com.example.samuel.pentrufacultate.products.models.get_stores_for_products.RetailStores;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductsApi {

    @GET("pmonsvc/Retail/GetCatalogProductsByNameNetwork?prodname=")
    Call<CatalogProducts> getAllProducts();

    @GET("pmonsvc/Retail/GetStoresForProductsByLatLon")
    Call<RetailStores> getStoresForProductsByLatLon(@Query("lat") String lat, @Query("lon") String lon, @Query("buffer") String distanceInMeters, @Query("csvprodids") String products, @Query("OrderBy") String orderBy);

}