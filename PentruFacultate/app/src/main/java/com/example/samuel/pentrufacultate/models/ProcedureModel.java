package com.example.samuel.pentrufacultate.models;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.gson.Gson;


public class ProcedureModel {
private String name;
private String data_creation;
private ArrayList<String> steps;


    public ProcedureModel(String name, String data_creation, ArrayList<String> steps) {
        this.name = name;
        this.data_creation = data_creation;
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData_creation() {
        return data_creation;
    }

    public void setData_creation(String data_creation) {
        this.data_creation = data_creation;
    }

    public ArrayList<String> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<String> steps) {
        this.steps = steps;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public static ProcedureModel fromJson(String jsonProcedure){
        Gson gson = new Gson();
        return gson.fromJson(jsonProcedure,ProcedureModel.class);
    }


}
