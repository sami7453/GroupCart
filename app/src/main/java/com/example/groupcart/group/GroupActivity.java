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
import com.example.groupcart.user.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {
    private TextInputEditText groupNameEditText;
    private EditText memberUsernameEditText;
    private MaterialButton addMemberButton, saveGroupButton;
    private RecyclerView rvMembers;

    private List<String> memberUsernames = new ArrayList<>();
    private MemberAdapter memberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        groupNameEditText = findViewById(R.id.etGroupName);
        memberUsernameEditText = findViewById(R.id.etMemberUsername);
        addMemberButton = findViewById(R.id.btnAddMember);
        saveGroupButton = findViewById(R.id.btnSaveGroup);
        rvMembers = findViewById(R.id.rvMembers);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        memberAdapter = new MemberAdapter(memberUsernames, username -> {
            memberUsernames.remove(username);
            memberAdapter.update(memberUsernames);
        });

        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        rvMembers.setAdapter(memberAdapter);

        addMemberButton.setOnClickListener(v -> addMember());
        saveGroupButton.setOnClickListener(v -> saveGroup());
    }

    private void addMember() {
        String username = memberUsernameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Nom de membre vide", Toast.LENGTH_SHORT).show();
            return;
        }

        if (memberUsernames.contains(username)) {
            Toast.makeText(this, "Ce membre est déjà ajouté", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = getUserByUsername(username);
        if (user == null) {
            Toast.makeText(this, "Cet utilisateur n’existe pas", Toast.LENGTH_SHORT).show();
            return;
        }

        memberUsernames.add(username);
        memberAdapter.update(memberUsernames);
        memberUsernameEditText.setText("");
    }

    private void saveGroup() {
        String groupName = groupNameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(groupName)) {
            Toast.makeText(this, "Nom du groupe requis", Toast.LENGTH_SHORT).show();
            return;
        }

        List<User> allUsers = Prefs.with(this).loadUsers();
        List<User> selectedMembers = new ArrayList<>();

        for (String username : memberUsernames) {
            User user = getUserByUsername(username, allUsers);
            if (user != null) {
                selectedMembers.add(user);
            }
        }

        Group newGroup = new Group(groupName);
        for (User member : selectedMembers) {
            newGroup.addMember(member);
        }

        // Save group to global list
        List<Group> allGroups = Prefs.with(this).loadGroups();
        allGroups.add(newGroup);
        Prefs.with(this).saveGroups(allGroups);

        // Save group to each user's personal list
        for (User member : selectedMembers) {
            List<Group> userGroups = Prefs.with(this).loadGroupsForUser(member.getUsername());
            boolean alreadyInGroup = false;
            for (Group g : userGroups) {
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

    private User getUserByUsername(String username) {
        return getUserByUsername(username, Prefs.with(this).loadUsers());
    }

    private User getUserByUsername(String username, List<User> users) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
