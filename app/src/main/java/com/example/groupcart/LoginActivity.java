package com.example.groupcart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private Button   btnLogin;
    private Button   btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1) Lie les vues aux IDs du XML
        etUsername = findViewById(R.id.usernameEditText);
        etPassword = findViewById(R.id.passwordEditText);
        btnSignup  = findViewById(R.id.signupButton);
        btnLogin   = findViewById(R.id.loginButton);

        // 2) Passer à l'inscription
        btnSignup.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        // 3) Tentative de connexion
        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            if (Prefs.with(this).checkCredentials(user, pass)) {
                // Sauvegarde du user courant gérée par checkCredentials()
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(
                        this,
                        "Identifiants incorrects",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
