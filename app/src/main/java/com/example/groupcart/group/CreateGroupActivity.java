package com.example.groupcart.group;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.R;
import com.example.groupcart.user.UserModel;
import com.example.groupcart.user.UserRecyclerViewAdapter;
import com.example.groupcart.utils.Prefs;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {
    private TextInputEditText groupNameEditText;
    private EditText usernameEditText;
    private List<UserModel> memberUsers = new ArrayList<>();
    private UserRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        groupNameEditText = findViewById(R.id.groupNameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);

        // Top bar
        MaterialToolbar toolbar = findViewById(R.id.topBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // User recycler view
        RecyclerView userRecyclerView = findViewById(R.id.userRecyclerView);
        adapter = new UserRecyclerViewAdapter(this, memberUsers);
        userRecyclerView.setAdapter(adapter);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add member button
        MaterialButton addMemberButton = findViewById(R.id.addUserButton);
        addMemberButton.setOnClickListener(v -> onAddMember());

        // Save group button
        MaterialButton saveGroupButton = findViewById(R.id.saveGroupButton);
        saveGroupButton.setOnClickListener(v -> onSaveGroup());
    }

    private void onAddMember() {
        String username = usernameEditText.getText().toString().trim();
        String me = Prefs.with(this).getCurrentUser();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Empty username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.equals(me)) {
            Toast.makeText(this, "You cannot add yourself", Toast.LENGTH_SHORT).show();
            return;
        }

        for (UserModel user : memberUsers) {
            if (user.getUsername().equals(username)) {
                Toast.makeText(this, "User already in group", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        UserModel user = getUserByUsername(username);
        if (user == null) {
            Toast.makeText(this, "User doesn't exist", Toast.LENGTH_SHORT).show();
            return;
        }

        memberUsers.add(user);
        adapter.notifyItemInserted(memberUsers.size() - 1);
        usernameEditText.setText("");
    }

    private void onSaveGroup() {
        String groupName = groupNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(groupName)) {
            Toast.makeText(this, "Group name can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        GroupModel newGroup = new GroupModel(groupName);
        String me = Prefs.with(this).getCurrentUser();
        newGroup.getMembers().add(new UserModel(me, null, null));
        newGroup.getMembers().addAll(memberUsers);

        Prefs prefs = Prefs.with(this);
        List<GroupModel> myGroups = prefs.loadGroupsForUser(me);
        if (myGroups == null) {
            myGroups = new ArrayList<>();
        }

        myGroups.add(newGroup);
        prefs.saveGroupsForUser(me, myGroups);

        for (UserModel member : memberUsers) {
            List<GroupModel> mg = prefs.loadGroupsForUser(member.getUsername());
            if (mg == null) {
                mg = new ArrayList<>();
            }
            mg.add(newGroup);
            prefs.saveGroupsForUser(member.getUsername(), mg);
        }

        Toast.makeText(this, "Group created", Toast.LENGTH_SHORT).show();
        finish();
    }

    private UserModel getUserByUsername(String username) {
        for (UserModel user : Prefs.with(this).loadUsers()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
