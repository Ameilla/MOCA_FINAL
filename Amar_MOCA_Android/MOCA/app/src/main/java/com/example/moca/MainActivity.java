package com.example.moca;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
//import android.support.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final long DELAY_TIME_MS = 2000; // 2 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Using a Handler to post a delayed Runnable
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the new activity after the delay
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);
                finish(); // finish current activity if you don't want to come back to it
            }
        }, DELAY_TIME_MS);
    }
}
