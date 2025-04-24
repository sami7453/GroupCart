package com.example.groupcart;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;  // ou MaterialToolbar si tu préfères
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.group.GroupModel;
import com.example.groupcart.group.NewGroupActivity;
import com.example.groupcart.group.GroupRecyclerViewAdapter;
import com.example.groupcart.user.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView rvGroups;
    private GroupRecyclerViewAdapter groupAdapter;
    private FloatingActionButton fabAddGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Barre d'outils avec logout
        Toolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setTitle("GroupCart");
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        // RecyclerView des groupes
        rvGroups = findViewById(R.id.recyclerViewGroups);
        rvGroups.setLayoutManager(new LinearLayoutManager(this));
        groupAdapter = new GroupRecyclerViewAdapter(Prefs.with(this).loadGroups());
        rvGroups.setAdapter(groupAdapter);

        // FloatingActionButton pour créer un groupe
        fabAddGroup = findViewById(R.id.fabAddGroup);
        fabAddGroup.setOnClickListener(v ->
                startActivity(new Intent(this, NewGroupActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharge la liste des groupes après chaque retour
        List<GroupModel> groups = Prefs.with(this).loadGroups();
        groupAdapter.update(groups);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Déconnexion
            Prefs.with(this).clearCurrentUser();
            startActivity(new Intent(this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
