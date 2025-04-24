package com.example.groupcart.product;

import java.util.List;

public class ProductModel {
    private String name;
    private int price;
    private List<String> ingredients;

    public ProductModel(String name, int price, List<String> ingredients) {
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
}
