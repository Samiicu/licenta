package com.example.samuel.pentrufacultate.products.models.get_stores_for_products;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Catprod")
public class Catprod {

    @Element(name = "Prodcateg", required = false)
    private Prodcateg Prodcateg;

    @Element(name = "Id", required = false)
    private String Id;

    @Element(name = "Name", required = false)
    private String Name;

    public Prodcateg getProdcateg() {
        return Prodcateg;
    }

    public void setProdcateg(Prodcateg Prodcateg) {
        this.Prodcateg = Prodcateg;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    @Override
    public String toString() {
        return "ClassPojo [Prodcateg = " + Prodcateg + ", Id = " + Id + ", Name = " + Name + "]";
    }
}