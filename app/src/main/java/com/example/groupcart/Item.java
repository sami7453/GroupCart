package com.example.groupcart;

public class Item {
    private String name;
    private String ingredients;

    public Item(String name, String ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public String getIngredients() {
        return ingredients;
    }
}
