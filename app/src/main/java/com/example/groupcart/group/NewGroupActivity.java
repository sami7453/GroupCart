package com.example.groupcart.group;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupcart.MemberAdapter;
import com.example.groupcart.Prefs;
import com.example.groupcart.R;
import com.example.groupcart.user.UserModel;
import com.example.groupcart.user.UserRecyclerViewAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * View that allows the user to create a new group.
 */
public class NewGroupActivity extends AppCompatActivity {
    private TextInputEditText groupNameEditText;
    private EditText memberUsernameEditText;
    private MaterialButton addMemberButton, saveGroupButton;
    private RecyclerView memberList;
    private List<String> memberUsernames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        groupNameEditText = findViewById(R.id.groupNameEditText);
        memberUsernameEditText = findViewById(R.id.usernameEditText);
        addMemberButton = findViewById(R.id.addMemberButton);
        saveGroupButton = findViewById(R.id.saveButton);
        memberList = findViewById(R.id.memberList);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        UserRecyclerViewAdapter adapter = new UserRecyclerViewAdapter(this, ??);
        memberList.setAdapter(adapter);
        memberList.setLayoutManager(new LinearLayoutManager(this));

        addMemberButton.setOnClickListener(v -> onAddMember());
        saveGroupButton.setOnClickListener(v -> saveGroup());
    }

    private void onAddMember() {
        String username = memberUsernameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Empty username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (memberUsernames.contains(username)) {
            Toast.makeText(this, "User already in group", Toast.LENGTH_SHORT).show();
            return;
        }

        UserModel user = getUserByUsername(username);
        if (user == null) {
            Toast.makeText(this, "User doesn't exist", Toast.LENGTH_SHORT).show();
            return;
        }

        memberUsernames.add(username);
        memberAdapter.update(memberUsernames);
        memberUsernameEditText.setText("");
    }

    private onRemoveMember() {

    }

    private void saveGroup() {
        String groupName = groupNameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(groupName)) {
            Toast.makeText(this, "Nom du groupe requis", Toast.LENGTH_SHORT).show();
            return;
        }

        List<UserModel> allUsers = Prefs.with(this).loadUsers();
        List<UserModel> selectedMembers = new ArrayList<>();

        for (String username : memberUsernames) {
            UserModel user = getUserByUsername(username, allUsers);
            if (user != null) {
                selectedMembers.add(user);
            }
        }

        GroupModel newGroup = new GroupModel(groupName);
        for (UserModel member : selectedMembers) {
            newGroup.addMember(member);
        }

        // Save group to global list
        List<GroupModel> allGroups = Prefs.with(this).loadGroups();
        allGroups.add(newGroup);
        Prefs.with(this).saveGroups(allGroups);

        // Save group to each user's personal list
        for (UserModel member : selectedMembers) {
            List<GroupModel> userGroups = Prefs.with(this).loadGroupsForUser(member.getUsername());
            boolean alreadyInGroup = false;
            for (GroupModel g : userGroups) {
                if (g.getName().equals(groupName)) {
                    alreadyInGroup = true;
                    break;
                }
            }

            if (!alreadyInGroup) {
                userGroups.add(newGroup);
                Prefs.with(this).saveGroupsForUser(member.getUsername(), userGroups);
            }
        }

        Toast.makeText(this, "Groupe crée !", Toast.LENGTH_SHORT).show();
        finish();
    }

    private UserModel getUserByUsername(String username) {
        return getUserByUsername(username, Prefs.with(this).loadUsers());
    }

    private UserModel getUserByUsername(String username, List<UserModel> users) {
        for (UserModel user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
