package com.example.groupcart.group;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;  // ou MaterialToolbar si tu préfères
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GroupsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        // Top bar
        Toolbar topBar = findViewById(R.id.topBar);
        topBar.setTitle("GroupCart");
        topBar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        // Group recycler view
        RecyclerView groupRecyclerView = findViewById(R.id.groupRecyclerView);
        GroupRecyclerViewAdapter adapter = new GroupRecyclerViewAdapter(this, ??);
        groupRecyclerView.setAdapter(adapter);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create group button
        FloatingActionButton createGroupButton = findViewById(R.id.createGroupButton);
        createGroupButton.setOnClickListener(v ->
            startActivity(new Intent(this, CreateGroupActivity.class))
        );
    }
}
