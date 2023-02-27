package com.example.dronez_beta;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.Arrays;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {

    private static final int[] RC = {0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets the  background color white
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#000000"));
        }

        // Removes the action bar
        getSupportActionBar().hide();
    }

    // While the app is running
    @Override
    protected void onResume() {
        super.onResume();

        // Takes the strength value of the left joystick depending on the angle
        JoystickView leftjoystick = (JoystickView) findViewById(R.id.joystickViewLeft);
        leftjoystick.setOnMoveListener((angle, strength) -> {

            if (angle >45 && angle <=135){
                RC[2]= strength;
            }
            if (angle >226 && angle <=315){
                strength *= -1;
                RC[2]= strength;
            }
            if (angle >135 && angle <=225){
                strength *= -1;
                RC[3]= strength;
            }
            if (angle >316 && angle <=359 || angle >0 && angle <=45){
                RC[3]= strength;
            }

            // Send the strength data to drone using command
//            telloConnect("rc "+ RC[0] +" "+ RC[1] +" "+ RC[2] +" "+ RC[3]); // send the command eg,. 'rc 10 00 32 00'
            Arrays.fill(RC, 0); // reset the array with 0 after every virtual joystick move
        });
    }
}