package com.example.rahul.sih;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.SingleValueDataSet;
import com.anychart.charts.CircularGauge;
import com.anychart.enums.Anchor;
import com.anychart.graphics.vector.text.HAlign;

import org.json.JSONException;
import org.json.JSONObject;

public class pressure extends AppCompatActivity {

    AnyChartView anyChartView;
    RequestQueue requestQueue;
    String url;
    Boolean first = true;
    CircularGauge circularGauge;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        circularGauge = AnyChart.circular();
        progressBar.setVisibility(View.INVISIBLE);
        anyChartView = findViewById(R.id.anychart_view);
        requestQueue = Volley.newRequestQueue(this);
        url = "https://sensorapiturings.herokuapp.com/getCurrentPressure";
        sendjsonrequest(url);
    }

    public void sendjsonrequest(String url){
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String str = response.getString("currentPressure");
                    int temp = Integer.valueOf(str);
                    showPressure(temp);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Can't connect to server", Toast.LENGTH_SHORT).show();
                    showPressure(50);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Can't connect to server", Toast.LENGTH_SHORT).show();
            }
        }
        );

        requestQueue.add(jsonObjectRequest);
    }

    void showPressure(double pressure)
    {
        progressBar.setVisibility(View.INVISIBLE);
        if(first)
        {
            CircularGauge circularGauge = AnyChart.circular();
            circularGauge.fill("#fff")
                    .stroke(null)
                    .padding(0, 0, 0, 0)
                    .margin(30, 30, 30, 30);
            circularGauge.startAngle(0)
                    .sweepAngle(360);

            double currentValue = 13.8D;
            circularGauge.data(new SingleValueDataSet(new Double[] { pressure }));

            circularGauge.axis(0)
                    .startAngle(-150)
                    .radius(80)
                    .sweepAngle(300)
                    .width(3)
                    .ticks("{ type: 'line', length: 4, position: 'outside' }");

            circularGauge.axis(0).labels().position("outside");

            circularGauge.axis(0).scale()
                    .minimum(95000)
                    .maximum(120000);

            circularGauge.axis(0).scale()
                    .ticks("{interval: 10}")
                    .minorTicks("{interval: 10}");

            circularGauge.needle(0)
                    .stroke(null)
                    .startRadius("6%")
                    .endRadius("38%")
                    .startWidth("2%")
                    .endWidth(0);

            circularGauge.cap()
                    .radius("4%")
                    .enabled(true)
                    .stroke(null);

            circularGauge.label(0)
                    .text("<span style=\"font-size: 25\">&nbsp&nbspPressure</span>")
                    .useHtml(true)
                    .hAlign(HAlign.CENTER);
            circularGauge.label(0)
                    .anchor(Anchor.CENTER_TOP)
                    .offsetY(100)
                    .padding(15, 20, 0, 0);

            circularGauge.label(1)
                    .text("<span style=\"font-size: 20\">" + pressure + "</span>")
                    .useHtml(true)
                    .hAlign(HAlign.CENTER);
            circularGauge.label(1)
                    .anchor(Anchor.CENTER_TOP)
                    .offsetY(-100)
                    .padding(5, 10, 0, 0)
                    .background("{fill: 'none', stroke: '#c1c1c1', corners: 3, cornerType: 'ROUND'}");

            circularGauge.range(0,
                    "{\n" +
                            "    from: 0,\n" +
                            "    to: 25,\n" +
                            "    position: 'inside',\n" +
                            "    fill: 'green 0.5',\n" +
                            "    stroke: '1 #000',\n" +
                            "    startSize: 6,\n" +
                            "    endSize: 6,\n" +
                            "    radius: 80,\n" +
                            "    zIndex: 1\n" +
                            "  }");

            circularGauge.range(1,
                    "{\n" +
                            "    from: 80,\n" +
                            "    to: 140,\n" +
                            "    position: 'inside',\n" +
                            "    fill: 'red 0.5',\n" +
                            "    stroke: '1 #000',\n" +
                            "    startSize: 6,\n" +
                            "    endSize: 6,\n" +
                            "    radius: 80,\n" +
                            "    zIndex: 1\n" +
                            "  }");

            anyChartView.setChart(circularGauge);
            first = false;
        }
        else
        {
            circularGauge.data(new SingleValueDataSet(new Double[] { pressure }));
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
