package com.example.rahul.sih;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.SingleValueDataSet;
import com.anychart.charts.LinearGauge;
import com.anychart.enums.Anchor;
import com.anychart.enums.Orientation;
import com.anychart.enums.Position;
import com.anychart.scales.Base;
import com.anychart.scales.Linear;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class thermometer extends AppCompatActivity {

    AnyChartView anyChartView;
    RequestQueue requestQueue;
    String url;
    Boolean first = true;
    LinearGauge linearGauge;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermometer);
        anyChartView = findViewById(R.id.anychart_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
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
                    String str = response.getString("currentTemperature");
                    int temp = Integer.valueOf(str);
                    showTemperature(temp);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "err", Toast.LENGTH_SHORT).show();
                    showTemperature(50);
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

    void showTemperature(int temp)
    {
        progressBar.setVisibility(View.INVISIBLE);
        if(!first)
            linearGauge.data(new SingleValueDataSet(new Integer[] { temp }));

        else
        {
            linearGauge = AnyChart.linear();

            linearGauge.data(new SingleValueDataSet(new Integer[] { temp }));

            linearGauge.tooltip()
                    .useHtml(true)
                    .format(
                            "function () {\n" +
                                    "          return this.value + '&deg;' + 'C' +\n" +
                                    "            ' (' + (this.value * 1.8 + 32).toFixed(1) +\n" +
                                    "            '&deg;' + 'F' + ')'\n" +
                                    "    }");

            linearGauge.label(0).useHtml(true);
            linearGauge.label(0)
                    .text("C&deg;")
                    .position(Position.LEFT_BOTTOM)
                    .anchor(Anchor.LEFT_BOTTOM)
                    .offsetY("20px")
                    .offsetX("38%")
                    .fontColor("black")
                    .fontSize(17);

            linearGauge.label(1)
                    .useHtml(true)
                    .text("F&deg;")
                    .position(Position.RIGHT_BOTTOM)
                    .anchor(Anchor.RIGHT_BOTTOM)
                    .offsetY("20px")
                    .offsetX("47.5%")
                    .fontColor("black")
                    .fontSize(17);

            Base scale = linearGauge.scale()
                    .minimum(0)
                    .maximum(100);
//                .setTicks

            linearGauge.axis(0).scale(scale);
            linearGauge.axis(0)
                    .offset("-1%")
                    .width("0.5%");

            linearGauge.axis(0).labels()
                    .format("{%Value}&deg;")
                    .useHtml(true);

            linearGauge.thermometer(0)
                    .name("Thermometer")
                    .id(1);

            linearGauge.axis(0).minorTicks(true);
            linearGauge.axis(0).labels()
                    .format(
                            "function () {\n" +
                                    "    return '<span style=\"color:black;\">' + this.value + '&deg;</span>'\n" +
                                    "  }")
                    .useHtml(true);

            linearGauge.axis(1).minorTicks(true);
            linearGauge.axis(1).labels()
                    .format(
                            "function () {\n" +
                                    "    return '<span style=\"color:black;\">' + this.value + '&deg;</span>'\n" +
                                    "  }")
                    .useHtml(true);
            linearGauge.axis(1)
                    .offset("3.5%")
                    .orientation(Orientation.RIGHT);

            Linear linear = Linear.instantiate();
            linear.minimum(-20)
                    .maximum(100);
//                .setTicks
            linearGauge.axis(1).scale(linear);

            anyChartView.setChart(linearGauge);

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
