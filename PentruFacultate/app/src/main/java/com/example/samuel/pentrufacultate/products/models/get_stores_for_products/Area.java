package com.example.samuel.pentrufacultate.products.models.get_stores_for_products;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Area")
public class Area {
    @Element(name = "Points")
    private Points Points;

    public Points getPoints() {
        return Points;
    }

    public void setPoints(Points Points) {
        this.Points = Points;
    }

    @Override
    public String toString() {
        return "ClassPojo [Points = " + Points + "]";
    }
}