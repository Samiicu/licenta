package com.example.samuel.pentrufacultate.products.models.get_stores_for_products;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Addr")
public class Addr {

    @Element(name = "Addrstring", required = false)
    private String Addrstring;


    @Element(name = "Wkt", required = false)
    private String Wkt;


    @Element(name = "Uatid", required = false)
    private String Uatid;


    @Element(name = "Zipcode", required = false)
    private String Zipcode;


    @Element(name = "Contactdetails", required = false)
    private String Contactdetails;


    @Element(name = "Location", required = false)
    private Location Location;

    public String getAddrstring() {
        return Addrstring;
    }

    public void setAddrstring(String Addrstring) {
        this.Addrstring = Addrstring;
    }

    public String getWkt() {
        return Wkt;
    }

    public void setWkt(String Wkt) {
        this.Wkt = Wkt;
    }

    public String getUatid() {
        return Uatid;
    }

    public void setUatid(String Uatid) {
        this.Uatid = Uatid;
    }

    public String getZipcode() {
        return Zipcode;
    }

    public void setZipcode(String Zipcode) {
        this.Zipcode = Zipcode;
    }

    public String getContactdetails() {
        return Contactdetails;
    }

    public void setContactdetails(String Contactdetails) {
        this.Contactdetails = Contactdetails;
    }

    public Location getLocation() {
        return Location;
    }

    public void setLocation(Location Location) {
        this.Location = Location;
    }

    @Override
    public String toString() {
        return "ClassPojo [Addrstring = " + Addrstring + ", Wkt = " + Wkt + ", Uatid = " + Uatid + ", Zipcode = " + Zipcode + ", Contactdetails = " + Contactdetails + ", Location = " + Location + "]";
    }
}

