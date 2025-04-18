package com.example.groupcart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView rvGroups;
    private GroupAdapter adapter;
    private FloatingActionButton fabAddGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        rvGroups     = findViewById(R.id.recyclerViewGroups);
        fabAddGroup  = findViewById(R.id.fabAddGroup);

        // 1) Setup RecyclerView
        rvGroups.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupAdapter(Prefs.with(this).loadGroups());
        rvGroups.setAdapter(adapter);

        // 2) Bouton pour créer un nouveau groupe
        fabAddGroup.setOnClickListener(v -> {
            startActivity(new Intent(this, GroupActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharge la liste à chaque retour sur l'écran
        List<Group> groups = Prefs.with(this).loadGroups();
        adapter.update(groups);
    }
}
