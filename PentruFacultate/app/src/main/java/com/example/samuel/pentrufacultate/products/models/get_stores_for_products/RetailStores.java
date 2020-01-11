package com.example.samuel.pentrufacultate.products.models.get_stores_for_products;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "RetailStores")
public class RetailStores {

    @Element(name = "Area")
    private Area Area;

    @Element(name = "Areawkt")
    private String Areawkt;

    @Element(name = "Items")
    private Items Items;

    public Area getArea() {
        return Area;
    }

    public void setArea(Area Area) {
        this.Area = Area;
    }

    public String getAreawkt() {
        return Areawkt;
    }

    public void setAreawkt(String Areawkt) {
        this.Areawkt = Areawkt;
    }

    public Items getItems() {
        return Items;
    }

    public void setItems(Items Items) {
        this.Items = Items;
    }

    @Override
    public String toString() {
        return "ClassPojo [Area = " + Area + ", Areawkt = " + Areawkt + ", Items = " + Items + "]";
    }
}
