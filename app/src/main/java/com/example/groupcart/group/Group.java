package com.example.groupcart.group;

import com.example.groupcart.item.ItemList;
import com.example.groupcart.user.User;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private List<User> members;
    private List<ItemList> lists;

    public Group(String name) {
        this.name = name;
        this.members = new ArrayList<>();
        this.lists = new ArrayList<>();
    }

    public void addMember(User member) {
        if (member != null && !members.contains(member)) {
            members.add(member);
        }
    }

    public void removeUser(User member) {
        if (member != null) {
            members.remove(member);
        }
    }

    public String getName() { return name; }
    public List<User> getMembers() { return members; }
    public List<ItemList> getLists() { return lists; }
}
