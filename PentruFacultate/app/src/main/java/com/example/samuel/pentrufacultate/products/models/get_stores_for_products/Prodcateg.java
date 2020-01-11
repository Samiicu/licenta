package com.example.samuel.pentrufacultate.products.models.get_stores_for_products;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Prodcateg")
public class Prodcateg {

    @Element(name = "ParentId", required = false)
    private String ParentId;

    @Element(name = "Id", required = false)
    private String Id;

    @Element(name = "Logo", required = false)
    private Logo Logo;

    @Element(name = "Name", required = false)
    private String Name;

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String ParentId) {
        this.ParentId = ParentId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
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

    @Override
    public String toString() {
        return "ClassPojo [ParentId = " + ParentId + ", Id = " + Id + ", Logo = " + Logo + ", Name = " + Name + "]";
    }
}

