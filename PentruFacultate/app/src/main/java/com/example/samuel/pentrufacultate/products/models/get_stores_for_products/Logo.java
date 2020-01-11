package com.example.samuel.pentrufacultate.products.models.get_stores_for_products;

import org.simpleframework.xml.Element;

public class Logo {
    @Element(name = "Logouri", required = false)
    private String Logouri;

    public String getLogouri() {
        return Logouri;
    }

    public void setLogouri(String Logouri) {
        this.Logouri = Logouri;
    }

    @Override
    public String toString() {
        return "ClassPojo [Logouri = " + Logouri + "]";
    }
}
