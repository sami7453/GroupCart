package com.example.groupcart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.groupcart.utils.Prefs;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // Register button
        Button loginButton = findViewById(R.id.registerButton);
        loginButton.setOnClickListener(v -> onAttemptLogin());

        // Login button
        Button registerButton = findViewById(R.id.loginButton);
        registerButton.setOnClickListener(v ->
            startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void onAttemptLogin() {
        String user = usernameEditText.getText().toString().trim();
        String pass = passwordEditText.getText().toString().trim();

        if (Prefs.with(this).checkCredentials(user, pass)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Incorrect credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
