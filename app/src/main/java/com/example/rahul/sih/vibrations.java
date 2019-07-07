package com.example.rahul.sih;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class vibrations extends AppCompatActivity {


    AnyChartView anyChartView;
    RequestQueue requestQueue;
    String url;
    Boolean first = true;
    ProgressBar progressBar;
    int xcoordinate;
    List<DataEntry> seriesData = new ArrayList<>();
    Set set = Set.instantiate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibrations);
        anyChartView = findViewById(R.id.anychart_view);
        url = "https://sensorapiturings.herokuapp.com/getCurrentVibrations";
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        anyChartView = findViewById(R.id.anychart_view);
        requestQueue = Volley.newRequestQueue(this);
        sendjsonrequest(url);
    }

    public void sendjsonrequest(String url){
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String str = response.getString("currentX");
                    double x = Double.valueOf(str);
                    str = response.getString("currentY");
                    double y = Double.valueOf(str);
                    str = response.getString("currentZ");
                    double z = Double.valueOf(str);
                    showVibrations(x, y, z);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Can't connect to server", Toast.LENGTH_SHORT).show();
                    showVibrations(0,0,0);
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


    void showVibrations(double x, double y, double z)
    {
        progressBar.setVisibility(View.INVISIBLE);
        if(first)
        {
            first = false;
            Cartesian cartesian = AnyChart.line();

            cartesian.animation(true);

            cartesian.padding(10d, 20d, 5d, 20d);

            cartesian.crosshair().enabled(true);
            cartesian.crosshair()
                    .yLabel(true)
                    .yStroke((Stroke) null, null, null, (String) null, (String) null);

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

            cartesian.title("Trend of Sales of the Most Popular Products of ACME Corp.");

            cartesian.yAxis(0).title("Number of Bottles Sold (thousands)");
            cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

            seriesData.add(new CustomDataEntry("1986", 3.6, 2.3, 2.8));
            seriesData.add(new CustomDataEntry("1987", 7.1, 4.0, 4.1));
            seriesData.add(new CustomDataEntry("1988", 8.5, 6.2, 5.1));
            seriesData.add(new CustomDataEntry("1989", 9.2, 11.8, 6.5));
            seriesData.add(new CustomDataEntry("1990", 10.1, 13.0, 12.5));
            seriesData.add(new CustomDataEntry("1991", 11.6, 13.9, 18.0));
            seriesData.add(new CustomDataEntry("1992", 16.4, 18.0, 21.0));
            seriesData.add(new CustomDataEntry("1993", 18.0, 23.3, 20.3));

            set.data(seriesData);
            Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
            Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
            Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

            Line series1 = cartesian.line(series1Mapping);
            series1.name("Brandy");
            series1.hovered().markers().enabled(true);
            series1.hovered().markers()
                    .type(MarkerType.CIRCLE)
                    .size(4d);
            series1.tooltip()
                    .position("right")
                    .anchor(Anchor.LEFT_CENTER)
                    .offsetX(5d)
                    .offsetY(5d);

            Line series2 = cartesian.line(series2Mapping);
            series2.name("Whiskey");
            series2.hovered().markers().enabled(true);
            series2.hovered().markers()
                    .type(MarkerType.CIRCLE)
                    .size(4d);
            series2.tooltip()
                    .position("right")
                    .anchor(Anchor.LEFT_CENTER)
                    .offsetX(5d)
                    .offsetY(5d);

            Line series3 = cartesian.line(series3Mapping);
            series3.name("Tequila");
            series3.hovered().markers().enabled(true);
            series3.hovered().markers()
                    .type(MarkerType.CIRCLE)
                    .size(4d);
            series3.tooltip()
                    .position("right")
                    .anchor(Anchor.LEFT_CENTER)
                    .offsetX(5d)
                    .offsetY(5d);

            cartesian.legend().enabled(true);
            cartesian.legend().fontSize(13d);
            cartesian.legend().padding(0d, 0d, 10d, 0d);

            anyChartView.setChart(cartesian);
        }

        else
        {
            //seriesData.add(new CustomDataEntry(String.valueOf(xcoordinate++), x, y, z));
            //set.data(seriesData);
        }

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendjsonrequest(url);
            }
        }, 1000);*/
    }


    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value, Number value2, Number value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }

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
