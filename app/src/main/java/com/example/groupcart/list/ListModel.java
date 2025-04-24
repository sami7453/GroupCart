package com.example.groupcart.list;

import com.example.groupcart.product.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class ListModel {
    private String name;
    private List<ProductModel> products;

    public ListModel(String name) {
        this.name = name;
        this.products = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<ProductModel> getProducts() {
        return products;
    }
}
