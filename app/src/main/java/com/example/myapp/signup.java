package com.example.myapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

public class signup extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, confirmPasswordEditText, emailEditText;
    private Button registerButton;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.usernameSignup);
        passwordEditText = findViewById(R.id.passwordSignup);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordSignup);
        emailEditText = findViewById(R.id.emailSignup);
        registerButton = findViewById(R.id.registerButton);

        dbHandler = new DBHandler(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                final String confirmPassword = confirmPasswordEditText.getText().toString();
                final String email = emailEditText.getText().toString();

                if (isEmpty(username) || isEmpty(password) || isEmpty(confirmPassword) || isEmpty(email)) {
                    Toast.makeText(signup.this, "Please enter all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(signup.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbHandler.isUserExists(username)) {
                    showUserExistsAlert();
                } else {
                    dbHandler.addUserDetails(username, password, confirmPassword, email);
                    Toast.makeText(signup.this, "Registered successfully.", Toast.LENGTH_SHORT).show();
                    clearFields();
                    showBiometricRegistration();
                }
            }
        });
    }

    private boolean isEmpty(String text) {
        return text.trim().isEmpty();
    }

    private void clearFields() {
        usernameEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
        emailEditText.setText("");
    }

    private void showUserExistsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Already Exists");
        builder.setMessage("The username already exists. Do you want to proceed to login?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                navigateToLogin();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(signup.this, login.class);
        startActivity(intent);
    }


    private void showBiometricRegistration() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Register Biometric")
                    .setDescription("Register your biometric for authentication.")
                    .setNegativeButtonText("Skip")
                    .build();

            BiometricPrompt biometricPrompt = new BiometricPrompt(this, ContextCompat.getMainExecutor(this),
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errorCode, CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            // Handle error
                        }

                        @Override
                        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            Toast.makeText(signup.this, "Biometric Registered!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            // Handle authentication failure
                        }
                    });

            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(this, "Biometric authentication is not available.", Toast.LENGTH_SHORT).show();
        }
    }
}
