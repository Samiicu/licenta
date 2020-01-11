package com.example.samuel.pentrufacultate.products.models.get_stores_for_products;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Product")
public class Product {

    @Element(name = "Brand", required = false)
    private String Brand;

    @Element(name = "Catprod", required = false)
    private Catprod Catprod;

    @Element(name = "Pricedate", required = false)
    private String Pricedate;

    @Element(name = "Promo", required = false)
    private String Promo;

    @Element(name = "Retailcategid", required = false)
    private String Retailcategid;

    @Element(name = "Retailcategname", required = false)
    private String Retailcategname;

    @Element(name = "Storeid", required = false)
    private String Storeid;

    @Element(name = "Price", required = false)
    private String Price;

    @Element(name = "Id", required = false)
    private String Id;

    @Element(name = "Networkid", required = false)
    private String Networkid;

    @Element(name = "Unit", required = false)
    private String Unit;

    @Element(name = "Name", required = false)
    private String Name;

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String Brand) {
        this.Brand = Brand;
    }

    public Catprod getCatprod() {
        return Catprod;
    }

    public void setCatprod(Catprod Catprod) {
        this.Catprod = Catprod;
    }

    public String getPricedate() {
        return Pricedate;
    }

    public void setPricedate(String Pricedate) {
        this.Pricedate = Pricedate;
    }

    public String getPromo() {
        return Promo;
    }

    public void setPromo(String Promo) {
        this.Promo = Promo;
    }

    public String getRetailcategid() {
        return Retailcategid;
    }

    public void setRetailcategid(String Retailcategid) {
        this.Retailcategid = Retailcategid;
    }

    public String getRetailcategname() {
        return Retailcategname;
    }

    public void setRetailcategname(String Retailcategname) {
        this.Retailcategname = Retailcategname;
    }

    public String getStoreid() {
        return Storeid;
    }

    public void setStoreid(String Storeid) {
        this.Storeid = Storeid;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String Price) {
        this.Price = Price;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getNetworkid() {
        return Networkid;
    }

    public void setNetworkid(String Networkid) {
        this.Networkid = Networkid;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String Unit) {
        this.Unit = Unit;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    @Override
    public String toString() {
        return "ClassPojo [Brand = " + Brand + ", Catprod = " + Catprod + ", Pricedate = " + Pricedate + ", Promo = " + Promo + ", Retailcategid = " + Retailcategid + ", Retailcategname = " + Retailcategname + ", Storeid = " + Storeid + ", Price = " + Price + ", Id = " + Id + ", Networkid = " + Networkid + ", Unit = " + Unit + ", Name = " + Name + "]";
    }
}

