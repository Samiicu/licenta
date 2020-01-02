package com.example.samuel.pentrufacultate.models;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ShoppingList {
    private String title;
    private ArrayList<ShoppingItem> shoppingItems;

    public ShoppingList() {
        this.shoppingItems = new ArrayList<>();
    }

    public ShoppingList(String title, ArrayList<ShoppingItem> shoppingItems) {
        this.title = title;
        this.shoppingItems = shoppingItems;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<ShoppingItem> getShoppingItems() {
        return shoppingItems;
    }

    public void addItemToShoppingList(ShoppingItem shoppingItem) {
        shoppingItems.add(shoppingItem);
    }

    public ShoppingItem getItemFromPosition(int position) {
        return shoppingItems.get(position);
    }

    public void setSteps(ArrayList<ShoppingItem> shoppingItems) {
        this.shoppingItems = shoppingItems;
    }

    public int getSize() {
        return shoppingItems.size();
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public ShoppingList fromJson(String jsonProcedure) {
        Gson gson = new Gson();
        return gson.fromJson(jsonProcedure, ShoppingList.class);
    }

    public void removeItemFromShoppingList(String itemName) {
        for (ShoppingItem shoppingItem : shoppingItems) {
            if (shoppingItem.getName().equals(itemName)) {
                shoppingItems.remove(shoppingItem);
                return;
            }
        }
    }
}
