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

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // Register button
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> onAttemptRegister());
    }

    private void onAttemptRegister() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password length must be six at least", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = Prefs.with(this).addUser(new UserModel(username, password, email));

        if (!ok) {
            Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Successful register", Toast.LENGTH_SHORT).show();
        finish();
    }
}
