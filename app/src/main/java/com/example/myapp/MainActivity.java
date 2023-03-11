package com.example.myapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button signup = findViewById(R.id.signup);
        signup.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this, signup.class);
            startActivity(intent);
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button login = findViewById(R.id.login);
        login.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this, login.class);
            startActivity(intent);
        });
    }
}