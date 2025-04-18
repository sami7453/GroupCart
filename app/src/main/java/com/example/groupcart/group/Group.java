package com.example.groupcart.group;

import com.example.groupcart.list.ShoppingList;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private List<String> members;
    private List<ShoppingList> lists;

    public Group(String name, List<String> members) {
        this.name = name;
        this.members = members;
        this.lists = new ArrayList<>();
    }

    public List<ShoppingList> getLists() { return lists; }
    public void setLists(List<ShoppingList> lists) {
        this.lists = lists;
    }

    public String getName() { return name; }
    public List<String> getMembers() { return members; }
}
