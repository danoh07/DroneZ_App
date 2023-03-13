package com.example.dronez_beta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button log;
    private Button fly;
    private Button help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hides action bar
        getSupportActionBar().hide();

        log = findViewById(R.id.log);
        fly = findViewById(R.id.fly);
        help = findViewById(R.id.help);

        // When log is clicked -> open list of recordings or open album
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "log is clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // When fly is clicked -> open Control selection page
        fly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ControlSelection.class));
            }
        });

        // When help is clicked -> open page with detailed manual of how to control drone
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "help is clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}