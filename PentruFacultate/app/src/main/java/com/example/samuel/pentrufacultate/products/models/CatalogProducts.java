package com.example.samuel.pentrufacultate.products.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "CatalogProducts")
public class CatalogProducts {
    @Element(name = "xmlns", required = false)
    private String xmlns;
    @Element(name = "Items")
    private Items Items;

    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    public Items getItems() {
        return Items;
    }

    public void setItems(Items Items) {
        this.Items = Items;
    }

    @Override
    public String toString() {
        return "ClassPojo [xmlns = " + xmlns + ", Items = " + Items + "]";
    }
}
