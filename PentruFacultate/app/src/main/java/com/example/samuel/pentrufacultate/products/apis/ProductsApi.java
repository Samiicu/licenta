package com.example.samuel.pentrufacultate.products.apis;

import com.example.samuel.pentrufacultate.products.models.CatalogProducts;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ProductsApi {

    @GET("pmonsvc/Retail/GetCatalogProductsByNameNetwork?prodname=")
    Call<CatalogProducts> getAllProducts();
}