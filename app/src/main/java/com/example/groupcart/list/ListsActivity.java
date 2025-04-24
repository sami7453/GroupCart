package com.example.groupcart.list;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        // Top bar
        Toolbar topBar = findViewById(R.id.topBar);
        topBar.setTitle("GroupCart");
        topBar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        // List recycler view
        RecyclerView groupRecyclerView = findViewById(R.id.listRecyclerView);
        ListRecyclerViewAdapter adapter = new ListRecyclerViewAdapter(this, ??);
        groupRecyclerView.setAdapter(adapter);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create list button
        FloatingActionButton createGroupButton = findViewById(R.id.createListButton);
        createGroupButton.setOnClickListener(v ->
            startActivity(new Intent(this, CreateListActivity.class))
        );
    }
}
