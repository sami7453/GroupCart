package com.example.groupcart.product;

import java.util.List;

public class ProductModel {
    private String name;
    private int price, status;
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
        // TODO:
        double min = 1.99;
        double max = 19.99;
        return (int) (min + Math.random() * (max - min));
        // return price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
}
