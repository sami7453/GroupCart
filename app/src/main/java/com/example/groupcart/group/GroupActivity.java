package com.example.groupcart.group;

import android.os.Bundle;
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
    private List<String> members;
    private MemberAdapter memberAdapter;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_add_group);

        groupNameEditText = findViewById(R.id.etGroupName);
        memberUsernameEditText = findViewById(R.id.etMemberUsername);
        addMemberButton = findViewById(R.id.btnAddMember);
        saveGroupButton = findViewById(R.id.btnSaveGroup);
        rvMembers = findViewById(R.id.rvMembers);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        members = new ArrayList<>();
        memberAdapter = new MemberAdapter(members, username -> {
            members.remove(username);
            memberAdapter.update(members);
        });
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        rvMembers.setAdapter(memberAdapter);

        addMemberButton.setOnClickListener(v -> addMember());
        saveGroupButton.setOnClickListener(v -> saveGroup());
    }

    private void addMember() {
        String newUser = memberUsernameEditText.getText().toString().trim();
        if (newUser.isEmpty()) {
            Toast.makeText(this, "Nom de membre vide", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérification que l’utilisateur existe
        boolean exists = false;
        for (User u : Prefs.with(this).loadUsers()) {
            if (u.getUsername().equals(newUser)) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            Toast.makeText(this, "Cet utilisateur n’existe pas", Toast.LENGTH_SHORT).show();
            return;
        }

        members.add(newUser);
        memberAdapter.update(members);
        memberUsernameEditText.setText("");
    }

    private void saveGroup() {
        String name = groupNameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Nom du groupe requis", Toast.LENGTH_SHORT).show();
            return;
        }

        Group newGroup = new Group(name, new ArrayList<>(members));

        List<Group> myGroups = Prefs.with(this).loadGroups();
        myGroups.add(newGroup);
        Prefs.with(this).saveGroups(myGroups);

        for (String member : members) {
            List<Group> mGroups = Prefs.with(this).loadGroupsForUser(member);
            boolean already = false;
            for (Group g : mGroups) {
                if (g.getName().equals(name)) { already = true; break; }
            }

            if (!already) {
                mGroups.add(newGroup);
                Prefs.with(this).saveGroupsForUser(member, mGroups);
            }
        }

        Toast.makeText(this, "Groupe crée !", Toast.LENGTH_SHORT).show();
        finish();
    }
}
