package com.example.samuel.pentrufacultate.models;

public class ShoppingItem {
    private String name;
    private boolean checked;
    private String quantity;
    private String measure;

    public ShoppingItem() {
    }

    public ShoppingItem(String name) {
        this.name = name;
        checked = true;
        quantity = "1";
        measure = "buc";
    }


    public ShoppingItem(String name, boolean checked, String quantity, String measure) {
        this.name = name;
        this.checked = checked;
        this.quantity = quantity;
        this.measure = measure;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(ShoppingItem anotherItem) {
        if (anotherItem == null || anotherItem.isChecked() != this.isChecked() || !anotherItem.getMeasure().equals(this.getMeasure()) || !anotherItem.getQuantity().equals(this.getQuantity())) {
            return false;
        } else {
            return true;
        }
    }
}
