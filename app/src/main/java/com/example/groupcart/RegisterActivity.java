package com.example.groupcart;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private Button   btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.usernameEditText);
        etEmail    = findViewById(R.id.emailEditText);
        etPassword = findViewById(R.id.passwordEditText);
        btnSignup  = findViewById(R.id.signupButton);

        btnSignup.setOnClickListener(v -> attemptSignup());
    }

    private void attemptSignup() {
        String username = etUsername.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation basique
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Format d'email invalide", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sauvegarde
        Prefs.with(this).saveUser(username, password, email);
        Toast.makeText(this, "Inscription réussie !", Toast.LENGTH_SHORT).show();

        // Retour au login
        finish();
    }
}
