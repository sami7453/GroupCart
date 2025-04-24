package com.example.groupcart.product;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.AddListActivity;
import com.example.groupcart.Prefs;
import com.example.groupcart.R;
import com.example.groupcart.group.GroupModel;
import com.example.groupcart.list.ListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    public static final String EXTRA_GROUP = "groupName";

    private RecyclerView rvLists;
    private ListAdapter adapter;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // 1) Récupérer le nom du groupe **avant** d'utiliser toolbar.setTitle(...)
        groupName = getIntent().getStringExtra(EXTRA_GROUP);

        // 2) Toolbar : titre dynamique + retour
        Toolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setTitle("Listes de " + groupName);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 3) Lier le RecyclerView à l'ID **rvLists** (tel que défini dans activity_list.xml)
        rvLists = findViewById(R.id.rvLists);
        rvLists.setLayoutManager(new LinearLayoutManager(this));

        // 4) Charger les listes du groupe
        List<GroupModel> groups = Prefs.with(this)
                .loadGroupsForUser(Prefs.with(this).getCurrentUser());
        GroupModel myGroup = null;
        for (GroupModel g : groups) {
            if (g.getName().equals(groupName)) {
                myGroup = g;
                break;
            }
        }
        List<ProductList> lists = myGroup != null
                ? myGroup.getLists()
                : new ArrayList<>();

        // 5) Initialiser l'adaptateur
        adapter = new ListAdapter(lists);
        rvLists.setAdapter(adapter);

        // 6) FAB pour ajouter une nouvelle liste
        FloatingActionButton fab = findViewById(R.id.fabAddList);
        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, AddListActivity.class);
            i.putExtra(EXTRA_GROUP, groupName);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Met à jour le RecyclerView avec les éventuelles nouvelles listes
        List<ProductList> updated = Prefs.with(this)
                .loadGroupsForUser(Prefs.with(this).getCurrentUser())
                .stream()
                .filter(g -> g.getName().equals(groupName))
                .findFirst()
                .map(GroupModel::getLists)
                .orElse(new ArrayList<>());
        adapter.update(updated);
    }
}
