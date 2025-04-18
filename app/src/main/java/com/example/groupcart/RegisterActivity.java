package com.example.groupcart;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etEmail, etPassword;
    private Button   btnSignup;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_register);
        etUsername = findViewById(R.id.usernameEditText);
        etEmail    = findViewById(R.id.emailEditText);
        etPassword = findViewById(R.id.passwordEditText);
        btnSignup  = findViewById(R.id.signupButton);

        btnSignup.setOnClickListener(v -> {
            String u = etUsername.getText().toString().trim();
            String e = etEmail.getText().toString().trim();
            String p = etPassword.getText().toString().trim();
            if (u.isEmpty() || e.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
                return;
            }
            if (p.length() < 6) {
                Toast.makeText(this, "Mot de passe ≥ 6 caractères", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean ok = Prefs.with(this).addUser(new User(u, p, e));
            if (!ok) {
                Toast.makeText(this, "Ce nom d’utilisateur existe déjà", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
            finish(); // retour au Login
        });
    }
}
