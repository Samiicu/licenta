package com.example.samuel.pentrufacultate.products.models.get_stores_for_products;


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "Items")
public class Items {

    @ElementList(name = "RetailStore", inline = true)
    private List<RetailStore> retailStoreList;

    public List<RetailStore> getRetailStoreList() {
        return retailStoreList;
    }

    public void setRetailStore(List<RetailStore> retailStoreList) {
        this.retailStoreList = retailStoreList;
    }

    @Override
    public String toString() {
        return "ClassPojo [RetailStore = " + retailStoreList + "]";
    }
}

