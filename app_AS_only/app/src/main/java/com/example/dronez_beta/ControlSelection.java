package com.example.dronez_beta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class ControlSelection extends AppCompatActivity {

    private Button AutomaticControl;
    private Button ManualControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_selection);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#000000"));
        }

        AutomaticControl = findViewById(R.id.AutomaticControl);
        ManualControl = findViewById(R.id.ManualControl);

        // When Automatic control is clicked -> open Automatic control page
        AutomaticControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ControlSelection.this, "Automatic Control clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // When Manual control is clicked -> open Manual control page
        ManualControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ControlSelection.this, ManualControl.class));
            }
        });
    }
}