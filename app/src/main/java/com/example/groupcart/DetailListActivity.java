package com.example.groupcart;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DetailListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemDetail adapter;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_list);

        recyclerView = findViewById(R.id.rvItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        itemList.add(new Item("Chips", "Pommes de terre, huile, sel"));
        itemList.add(new Item("Pizza", "Farine, tomate, fromage"));
        itemList.add(new Item("Coca-Cola", "Eau gazeuse, sucre, colorant"));

        adapter = new ItemDetail(itemList);
        recyclerView.setAdapter(adapter);
    }
}
