package com.example.groupcart;

import java.util.List;

public class ShoppingList {
    private String name;
    private List<String> items;

    public ShoppingList(String name, List<String> items) {
        this.name = name;
        this.items = items;
    }

    public String getName() { return name; }
    public List<String> getItems() { return items; }
}
