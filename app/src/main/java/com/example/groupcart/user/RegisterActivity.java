package com.example.groupcart.user;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.groupcart.Prefs;
import com.example.groupcart.R;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signupButton = findViewById(R.id.signupButton);

        signupButton.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String u = usernameEditText.getText().toString().trim();
        String e = emailEditText.getText().toString().trim();
        String p = passwordEditText.getText().toString().trim();

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

        finish();
    }
}
