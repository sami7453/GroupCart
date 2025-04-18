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
    private MaterialButton     btnAddMember, btnSaveGroup;
    private RecyclerView       rvMembers;

    private List<String>       members;
    private MemberAdapter      memberAdapter;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_add_group);

        // Barre d'outils avec bouton retour
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Liage des vues
        etGroupName      = findViewById(R.id.etGroupName);
        etMemberUsername = findViewById(R.id.etMemberUsername);
        btnAddMember     = findViewById(R.id.btnAddMember);
        btnSaveGroup     = findViewById(R.id.btnSaveGroup);
        rvMembers        = findViewById(R.id.rvMembers);

        // Préparation de la liste et de l'adapter
        members       = new ArrayList<>();
        memberAdapter = new MemberAdapter(members, username -> {
            members.remove(username);
            memberAdapter.update(members);
        });
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        rvMembers.setAdapter(memberAdapter);

        // Bouton “Ajouter un membre”
        btnAddMember.setOnClickListener(v -> {
            String newUser = etMemberUsername.getText().toString().trim();
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
            etMemberUsername.setText("");
        });

        // Bouton “Enregistrer le groupe”
        btnSaveGroup.setOnClickListener(v -> {
            String name = etGroupName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Nom du groupe requis", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1) Création de l’objet Group
            Group newGroup = new Group(name, new ArrayList<>(members));

            // 2) Sauvegarde pour le user courant
            List<Group> myGroups = Prefs.with(this).loadGroups();
            myGroups.add(newGroup);
            Prefs.with(this).saveGroups(myGroups);

            // 3) Propagation aux autres membres
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

            Toast.makeText(this, "Groupe créé !", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
