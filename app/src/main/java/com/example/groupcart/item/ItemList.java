package com.example.groupcart.item;

import java.util.ArrayList;
import java.util.List;

public class ItemList {
    private String name;
    private List<Item> items;

    public ItemList(String name) {
        this.name = name;
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        if (item != null && !items.contains(item)) {
            items.add(item);
        }
    }

    public void removeItem(Item item) {
        if (item != null) {
            items.remove(item);
        }
    }

    public String getName() { return name; }
    public List<Item> getItems() { return items; }
}
