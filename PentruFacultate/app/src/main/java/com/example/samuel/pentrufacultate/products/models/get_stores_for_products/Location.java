package com.example.samuel.pentrufacultate.products.models.get_stores_for_products;


import org.simpleframework.xml.Element;

public class Location {

    @Element(name = "Lon", required = false)
    private String Lon;

    @Element(name = "Lat", required = false)
    private String Lat;

    public String getLon() {
        return Lon;
    }

    public void setLon(String Lon) {
        this.Lon = Lon;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String Lat) {
        this.Lat = Lat;
    }

    @Override
    public String toString() {
        return "ClassPojo [Lon = " + Lon + ", Lat = " + Lat + "]";
    }
}

