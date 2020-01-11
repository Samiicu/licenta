package com.example.samuel.pentrufacultate.products.models.get_stores_for_products;


import com.example.samuel.pentrufacultate.products.models.CatalogProduct;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "Products")
public class Products {
    @ElementList(name = "Product", inline = true)
    private List<Product> productList;

    public List<Product> getProductsList() {
        return productList;
    }

    public void setProduct(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public String toString() {
        return "ClassPojo [Product = " + productList + "]";
    }
}

