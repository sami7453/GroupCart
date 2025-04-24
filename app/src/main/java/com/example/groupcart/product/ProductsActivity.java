// app/src/main/java/com/example/groupcart/product/ProductsActivity.java
package com.example.groupcart.product;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.R;
import com.example.groupcart.list.CreateListActivity;
import com.example.groupcart.list.ListModel;
import com.example.groupcart.list.ListRecyclerViewAdapter;
import com.example.groupcart.group.GroupModel;
import com.example.groupcart.utils.Prefs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    public static final String EXTRA_GROUP = "groupName";

    private RecyclerView rvLists;
    private ListRecyclerViewAdapter adapter;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        // Récupérer le nom du groupe
        groupName = getIntent().getStringExtra(EXTRA_GROUP);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.topBar);
        toolbar.setTitle("Listes de " + groupName);
        toolbar.setNavigationOnClickListener(v -> finish());

        // RecyclerView
        rvLists = findViewById(R.id.listRecyclerView);
        rvLists.setLayoutManager(new LinearLayoutManager(this));

        // FAB pour créer une liste
        FloatingActionButton fab = findViewById(R.id.createListButton);
        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, CreateListActivity.class);
            i.putExtra(EXTRA_GROUP, groupName);
            startActivity(i);
        });

        loadAndDisplayLists();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayLists();
    }

    private void loadAndDisplayLists() {
        List<GroupModel> groups = Prefs.with(this)
                .loadGroupsForUser(Prefs.with(this).getCurrentUser());
        GroupModel myGroup = null;
        for (GroupModel g : groups) {
            if (g.getName().equals(groupName)) {
                myGroup = g;
                break;
            }
        }
        List<ListModel> lists = (myGroup != null)
                ? myGroup.getLists()
                : new ArrayList<>();

        if (adapter == null) {
            adapter = new ListRecyclerViewAdapter(
                    this, groupName, lists
            );
            rvLists.setAdapter(adapter);
        } else {
            adapter = new ListRecyclerViewAdapter(
                    this, groupName, lists
            );
            rvLists.setAdapter(adapter);
        }
    }
}
