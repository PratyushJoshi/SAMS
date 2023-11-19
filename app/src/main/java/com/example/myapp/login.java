package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class login extends AppCompatActivity {

    private DBHandler dbHandler;
    private String currentUsername;
    private String currentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHandler = new DBHandler(this);

        final TextView registerNowBtn = findViewById(R.id.registerNowBtn);
        registerNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opensignup();
            }
        });

        // Biometric login
        Button btn_fp = findViewById(R.id.btn_fp);
        btn_fp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPasswordAndBiometric();
            }
        });
    }

    private void checkPasswordAndBiometric() {
        // Fetch username and password from UI elements
        EditText usernameEditText = findViewById(R.id.username1);
        EditText passwordEditText = findViewById(R.id.password1);
        String enteredUsername = usernameEditText.getText().toString();
        String enteredPassword = passwordEditText.getText().toString();

        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if entered username exists in the database
        if (dbHandler.authenticateUser(enteredUsername, enteredPassword)) {
            currentUsername = enteredUsername;
            currentPassword = enteredPassword;

            // Prompt for biometric authentication
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Verify")
                    .setDescription("User Authentication is required to proceed")
                    .setNegativeButtonText("Cancel")
                    .build();

            getBiometricPrompt().authenticate(promptInfo);
        } else {
            notifyUser("Wrong Password or Username!");
        }
    }

    private BiometricPrompt getBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser(errString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                openHomePage(); // Redirect to homepage on successful biometric authentication
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser("Biometric Authentication Failed!");
            }
        };

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, callback);
        return biometricPrompt;
    }

    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void opensignup() {
        Intent intent = new Intent(this, signup.class);
        startActivity(intent);
    }

    public void openHomePage() {
        Intent intent = new Intent(this, homepage.class);
        startActivity(intent);
        finish(); // Close the login/signup activity to prevent returning to it
    }
}
