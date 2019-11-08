package com.example.samuel.pentrufacultate.models;

import android.text.TextUtils;
import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;


public class RecipeModel {
    private String title;
    private String data_creation;
    private ArrayList<String> steps;


    public RecipeModel(String title, String data_creation, ArrayList<String> steps) {
        this.title = title;
        this.data_creation = data_creation;
        this.steps = steps;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public boolean isValid() {
        if (TextUtils.isEmpty(title) || steps.isEmpty() || TextUtils.isEmpty(data_creation)) {
            return false;
        } else {
            return true;
        }
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static RecipeModel fromJson(String jsonProcedure) {
        Gson gson = new Gson();
        return gson.fromJson(jsonProcedure, RecipeModel.class);
    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }
}
