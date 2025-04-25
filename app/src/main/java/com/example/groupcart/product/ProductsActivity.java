package com.example.groupcart.product;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.R;
import com.example.groupcart.group.GroupModel;
import com.example.groupcart.list.ListModel;
import com.example.groupcart.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    public static final String EXTRA_LIST_NAME = "listName";
    private String listName;
    private RecyclerView productRecyclerView;
    private ProductRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        listName = getIntent().getStringExtra(EXTRA_LIST_NAME);

        // Top bar
        Toolbar topBar = findViewById(R.id.topBar);
        topBar.setTitle("Products in " + listName);
        topBar.setNavigationOnClickListener(v -> finish());

        // Product recycler view
        productRecyclerView = findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadAndDisplayProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayProducts();
    }

    private void loadAndDisplayProducts() {
        Prefs prefs = Prefs.with(this);
        List<GroupModel> groups = prefs.loadGroupsForUser(prefs.getCurrentUser());
        ListModel myList = null;

        for (GroupModel group : groups) {
            for (ListModel list : group.getLists()) {
                if (list.getName().equals(listName)) {
                    myList = list;
                    break;
                }
            }
        }

        List<ProductModel> products = (myList != null ) ? myList.getProducts() : new ArrayList<>();

        adapter = new ProductRecyclerViewAdapter(this, myList, products);
        productRecyclerView.setAdapter(adapter);
    }
}
