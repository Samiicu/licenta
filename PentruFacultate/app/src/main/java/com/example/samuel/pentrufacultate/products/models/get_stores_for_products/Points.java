package com.example.samuel.pentrufacultate.products.models.get_stores_for_products;


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;
@Root(name ="Points")
public class Points
{
    @ElementList( name = "WgsPoint", inline = true)
    private List<WgsPoint>  wgsPointList;


    public List<WgsPoint> getWgsPointList ()
    {
        return wgsPointList;
    }

    public void setWgsPoint (List<WgsPoint> wgsPointList )
    {
        this.wgsPointList = wgsPointList;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [WgsPoint = "+wgsPointList+"]";
    }
}

