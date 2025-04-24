package com.example.groupcart.list;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.R;
import com.example.groupcart.group.GroupModel;
import com.example.groupcart.list.ListModel;
import com.example.groupcart.list.ListRecyclerViewAdapter;
import com.example.groupcart.list.CreateListActivity;
import com.example.groupcart.utils.Prefs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ListsActivity extends AppCompatActivity {
    public static final String EXTRA_GROUP = "groupName";

    private String groupName;
    private RecyclerView listRecyclerView;
    private ListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        // Récupérer le nom du groupe
        groupName = getIntent().getStringExtra(EXTRA_GROUP);

        // Barre supérieure
        Toolbar topBar = findViewById(R.id.topBar);
        topBar.setTitle("Listes de " + groupName);
        topBar.setNavigationOnClickListener(v -> finish());

        // Initialisation du RecyclerView
        listRecyclerView = findViewById(R.id.listRecyclerView);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Bouton de création de liste
        FloatingActionButton fab = findViewById(R.id.createListButton);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateListActivity.class);
            intent.putExtra(EXTRA_GROUP, groupName);
            startActivity(intent);
        });

        // Affichage initial
        loadAndDisplayLists();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayLists();
    }

    private void loadAndDisplayLists() {
        // Charger les listes du groupe depuis les prefs
        List<GroupModel> groups = Prefs.with(this)
                .loadGroupsForUser(Prefs.with(this).getCurrentUser());
        GroupModel myGroup = null;
        for (GroupModel g : groups) {
            if (g.getName().equals(groupName)) {
                myGroup = g;
                break;
            }
        }
        List<ListModel> lists = myGroup != null
                ? myGroup.getLists()
                : new ArrayList<>();

        // Initialiser ou mettre à jour l'adaptateur
        if (adapter == null) {
            adapter = new ListRecyclerViewAdapter(this, groupName, lists);
            listRecyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(lists);
            adapter.notifyDataSetChanged();
        }
    }
}
