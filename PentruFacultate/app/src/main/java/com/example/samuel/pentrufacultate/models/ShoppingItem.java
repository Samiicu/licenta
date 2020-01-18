package com.example.samuel.pentrufacultate.models;

public class ShoppingItem {
    private String name;
    private boolean checked;


    public ShoppingItem() {
    }

    public ShoppingItem(String name) {
        this.name = name;
        checked = true;

    }


    public ShoppingItem(String name, boolean checked, String quantity, String measure) {
        this.name = name;
        this.checked = checked;

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
        if (anotherItem == null || anotherItem.isChecked() != this.isChecked() ) {
            return false;
        } else {
            return true;
        }
    }
}
