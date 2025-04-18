package com.example.groupcart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    public static final String EXTRA_GROUP = "groupName";

    private RecyclerView rvLists;
    private ShoppingListAdapter adapter;
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
        List<Group> groups = Prefs.with(this)
                .loadGroupsForUser(Prefs.with(this).getCurrentUser());
        Group myGroup = null;
        for (Group g : groups) {
            if (g.getName().equals(groupName)) {
                myGroup = g;
                break;
            }
        }
        List<ShoppingList> lists = myGroup != null
                ? myGroup.getLists()
                : new ArrayList<>();

        // 5) Initialiser l'adaptateur
        adapter = new ShoppingListAdapter(lists);
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
        List<ShoppingList> updated = Prefs.with(this)
                .loadGroupsForUser(Prefs.with(this).getCurrentUser())
                .stream()
                .filter(g -> g.getName().equals(groupName))
                .findFirst()
                .map(Group::getLists)
                .orElse(new ArrayList<>());
        adapter.update(updated);
    }
}
