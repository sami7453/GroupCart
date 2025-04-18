package com.example.groupcart;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {
    private TextInputEditText etGroupName;
    private EditText           etMemberUsername;
    private MaterialButton     btnAddMember;
    private MaterialButton     btnSaveGroup;
    private RecyclerView       rvMembers;

    private List<String>       members;
    private MemberAdapter      memberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);


        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

            // 1) Récupération des vues
        etGroupName      = findViewById(R.id.etGroupName);
        etMemberUsername = findViewById(R.id.etMemberUsername);
        btnAddMember     = findViewById(R.id.btnAddMember);
        btnSaveGroup     = findViewById(R.id.btnSaveGroup);
        rvMembers        = findViewById(R.id.rvMembers);

        // 2) Préparation du RecyclerView
        members        = new ArrayList<>();
        memberAdapter  = new MemberAdapter(members);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        rvMembers.setAdapter(memberAdapter);

        // 3) Ajout d'un membre à la liste
        btnAddMember.setOnClickListener(v -> {
            String username = etMemberUsername.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(this, "Nom de membre vide", Toast.LENGTH_SHORT).show();
                return;
            }
            members.add(username);
            memberAdapter.update(members);
            etMemberUsername.setText("");
        });

        // 4) Sauvegarde du groupe complet
        btnSaveGroup.setOnClickListener(v -> {
            String groupName = etGroupName.getText().toString().trim();
            if (groupName.isEmpty()) {
                Toast.makeText(this, "Donnez un nom au groupe", Toast.LENGTH_SHORT).show();
                return;
            }
            // Enregistrement local
            List<Group> groups = Prefs.with(this).loadGroups();
            groups.add(new Group(groupName, new ArrayList<>(members)));
            Prefs.with(this).saveGroups(groups);

            Toast.makeText(this, "Groupe créé !", Toast.LENGTH_SHORT).show();
            finish();  // Retour à HomeActivity
        });
    }
}
