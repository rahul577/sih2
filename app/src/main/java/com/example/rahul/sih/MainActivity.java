package com.example.rahul.sih;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button thermometer_button = findViewById(R.id.thermometer_button);
        thermometer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), thermometer.class);
                startActivity(intent);
            }
        });

        Button pressure_button = findViewById(R.id.pressure_button);
        pressure_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), pressure.class);
                startActivity(intent);
            }
        });

        Button humidity_button = findViewById(R.id.humidity_button);
        humidity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), humidity.class);
                startActivity(intent);
            }
        });
    }
}
