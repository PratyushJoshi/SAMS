package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class signup extends AppCompatActivity {


    private BiometricPrompt getPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser(errString.toString());
            }

            private void notifyUser(String toString) {
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser("Authentication Successful!");
                Intent intent = new Intent(signup.this, homepage.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser("Authentication Failed!");
            }
        };

        return new BiometricPrompt(this, executor, callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final EditText usernameSignup = findViewById(R.id.usernameSignup);
        final EditText passwordSignup = findViewById(R.id.passwordSignup);
        final EditText confirmpasswordSignup = findViewById(R.id.confirmpasswordSignup);

        final Button registerBtn = findViewById(R.id.register);
        final TextView loginNowBtn = findViewById(R.id.loginNow);
        //loginNowBtn click to login page
        loginNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openlogin();
            }
        });



        registerBtn.setOnClickListener(view -> {

            //get data from EditTexts in signup xml page into string variables
            final String usernameSignuptxt = usernameSignup.getText().toString();
            final String passwordSignuptxt = passwordSignup.getText().toString();
            final String confirmpasswordSignuptxt = confirmpasswordSignup.getText().toString();

            // check if user filled all the fields before sending data to firebase
            // if (usernameSignuptxt.isEmpty() || passwordSignuptxt.isEmpty() || confirmpasswordSignuptxt.isEmpty()) {
            //Toast.makeText(signup.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            //  }
            //check if passwords are matching with each other
            //if not matching, then show toast message
            // else if (!passwordSignuptxt.equals(confirmpasswordSignuptxt)) {
            // Toast.makeText(signup.this, "Passwords are not matching", Toast.LENGTH_SHORT).show();
            //  } else {


        });

    }
    //loginNowBtn code
    public void openlogin(){
        Intent intent = new Intent(this,login.class);
        startActivity(intent);

    }
}
