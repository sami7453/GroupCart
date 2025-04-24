package com.example.groupcart.group;

import com.example.groupcart.list.ListModel;
import com.example.groupcart.user.UserModel;

import java.util.ArrayList;
import java.util.List;

public class GroupModel {
    private String name;
    private List<UserModel> members;
    private List<ListModel> lists;

    public GroupModel(String name) {
        this.name = name;
        this.members = new ArrayList<>();
        this.lists = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<UserModel> getMembers() {
        return members;
    }

    public List<ListModel> getLists() {
        if (lists == null) {
            lists = new ArrayList<>();
        }
        return lists;
    }
}
