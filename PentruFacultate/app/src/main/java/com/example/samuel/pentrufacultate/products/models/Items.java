package com.example.samuel.pentrufacultate.products.models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name ="Items")
public class Items
{
    @ElementList( name = "CatalogProduct", inline = true)
    private List<CatalogProduct> catalogProductList;
   public List<CatalogProduct> getCatalogProductList()
    {
        return catalogProductList;
    }

    public void setCatalogProductList(List<CatalogProduct> CatalogProduct)
    {
        this.catalogProductList = CatalogProduct;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [CatalogProduct = "+ catalogProductList +"]";
    }
}
