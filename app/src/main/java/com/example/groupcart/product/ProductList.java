package com.example.groupcart.product;

import java.util.ArrayList;
import java.util.List;

public class ProductList {
    private String name;

    private List<Product> products;

    public ProductList(String name) {
        this.name = name;
        this.products = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        if (product != null && !products.contains(product)) {
            products.add(product);
        }
    }

    public void removeProduct(Product product) {
        if (product != null) {
            products.remove(product);
        }
    }
}
