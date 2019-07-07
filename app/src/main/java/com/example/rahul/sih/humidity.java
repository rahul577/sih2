package com.example.rahul.sih;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class humidity extends AppCompatActivity {

    AnyChartView anyChartView;
    RequestQueue requestQueue;
    String url;
    Boolean first = true;
    Pie pie;
    ProgressBar progressBar;
    TextView currentTemperature, currentPpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity);
        pie = AnyChart.pie();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        anyChartView = findViewById(R.id.anychart_view);
        currentTemperature = findViewById(R.id.currentTemperature);
        currentPpm = findViewById(R.id.currentPpm);
        progressBar.setVisibility(View.VISIBLE);
        requestQueue = Volley.newRequestQueue(this);
        url = "https://sensorapiturings.herokuapp.com/getCurrentTemperature";
        sendjsonrequest(url);
    }

    public void sendjsonrequest(String url){
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String hum = response.getString("currentHumidity");
                    int humidity = Integer.valueOf(hum);
                    String temp = response.getString("currentTemperature");
                    int temperature = Integer.valueOf(temp);
                    temp = response.getString("currentPpm");
                    double ppm = Double.valueOf(temp);
                    int intppm = (int) ppm;
                    showHumidity(humidity, temperature, intppm);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Can't connect to internet", Toast.LENGTH_SHORT).show();
                    showHumidity(0, 0, 0);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
            }
        }
        );

        requestQueue.add(jsonObjectRequest);
    }

    void showHumidity(int humidity, int temperature, int ppm)
    {
        currentTemperature.setText(String.valueOf(temperature) + "°");
        currentPpm.setText(String.valueOf(ppm));
        progressBar.setVisibility(View.INVISIBLE);
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Humidity", humidity));
        data.add(new ValueDataEntry("", 100 - humidity));

        pie.data(data);

        if(first)
        {
            anyChartView.setChart(pie);
            first = false;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendjsonrequest(url);
            }
        }, 5000);
    }

    public void openHumidity(View view) {
        Intent intent = new Intent(getApplicationContext(), humidity.class);
        startActivity(intent);
    }


    public void openTemperature(View view) {
        Intent intent = new Intent(getApplicationContext(), thermometer.class);
        startActivity(intent);
    }


    public void openPressure(View view) {
        Intent intent = new Intent(getApplicationContext(), pressure.class);
        startActivity(intent);
    }

    public void openVibrations(View view) {
        Intent intent = new Intent(getApplicationContext(), vibrations.class);
        startActivity(intent);
    }
}
