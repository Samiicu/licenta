package com.example.samuel.pentrufacultate.products.models.get_stores_for_products;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "RetailStore")
public class RetailStore {

    @Element(name = "Workhours", required = false)
    private String Workhours;

    @Element(name = "Products", required = false)
    private Products Products;

    @Element(name = "Type")
    private Type Type;

    @Element(name = "Lastupdate", required = false)
    private String Lastupdate;

    @Element(name = "Id", required = false)
    private String Id;

    @Element(name = "Addr")
    private Addr Addr;

    @Element(name = "Distance", required = false)
    private String Distance;

    @Element(name = "Basketprice", required = false)
    private String Basketprice;

    @Element(name = "Logo")
    private Logo Logo;

    @Element(name = "Name", required = false)
    private String Name;

    @Element(name = "Retailnetwork")
    private Retailnetwork Retailnetwork;

    public String getWorkhours() {
        return Workhours;
    }

    public void setWorkhours(String Workhours) {
        this.Workhours = Workhours;
    }

    public Products getProducts() {
        return Products;
    }

    public void setProducts(Products Products) {
        this.Products = Products;
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type = Type;
    }

    public String getLastupdate() {
        return Lastupdate;
    }

    public void setLastupdate(String Lastupdate) {
        this.Lastupdate = Lastupdate;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public Addr getAddr() {
        return Addr;
    }

    public void setAddr(Addr Addr) {
        this.Addr = Addr;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String Distance) {
        this.Distance = Distance;
    }

    public String getBasketprice() {
        return Basketprice;
    }

    public void setBasketprice(String Basketprice) {
        this.Basketprice = Basketprice;
    }

    public Logo getLogo() {
        return Logo;
    }

    public void setLogo(Logo Logo) {
        this.Logo = Logo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public Retailnetwork getRetailnetwork() {
        return Retailnetwork;
    }

    public void setRetailnetwork(Retailnetwork Retailnetwork) {
        this.Retailnetwork = Retailnetwork;
    }


    @Override
    public String toString() {
        return "ClassPojo [Workhours = " + Workhours + ", Products = " + Products + ", Type = " + Type + ", Lastupdate = " + Lastupdate + ", Id = " + Id + ", Addr = " + Addr + ", Distance = " + Distance + ", Basketprice = " + Basketprice + ", Logo = " + Logo + ", Name = " + Name + ", Retailnetwork = " + Retailnetwork + "]";
    }
}

