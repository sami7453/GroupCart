package com.example.groupcart.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.R;
import com.example.groupcart.LoginActivity;
import com.example.groupcart.group.GroupModel;
import com.example.groupcart.group.GroupRecyclerViewAdapter;
import com.example.groupcart.utils.Prefs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class GroupsActivity extends AppCompatActivity {
    private RecyclerView groupRecyclerView;
    private GroupRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        // Top bar
        Toolbar topBar = findViewById(R.id.topBar);
        setSupportActionBar(topBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("GroupCart");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // Group recycler view
        groupRecyclerView = findViewById(R.id.groupRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<GroupModel> groups = Prefs.with(this).loadGroupsForUser(Prefs.with(this).getCurrentUser());
        if (groups == null) {
            groups = new ArrayList<>();
        }
        adapter = new GroupRecyclerViewAdapter(this, groups);
        groupRecyclerView.setAdapter(adapter);

        // Create group button
        FloatingActionButton createGroupButton = findViewById(R.id.createGroupButton);
        createGroupButton.setOnClickListener(v ->
            startActivity(new Intent(this, CreateGroupActivity.class))
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            Prefs.with(this).clearCurrentUser();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<GroupModel> groups = Prefs.with(this).loadGroupsForUser(Prefs.with(this).getCurrentUser());
        if (groups == null) {
            groups = new ArrayList<>();
        }
        adapter = new GroupRecyclerViewAdapter(this, groups);
        groupRecyclerView.setAdapter(adapter);
    }
}
