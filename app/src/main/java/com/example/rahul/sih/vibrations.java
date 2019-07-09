package com.example.rahul.sih;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class vibrations extends AppCompatActivity {

    Boolean first = true;
    ProgressBar progressBar;
    List<DataEntry> seriesData = new ArrayList<>();
    WebSocket ws;
    private OkHttpClient client;
    int val = 0;
    ArrayList<Entry> yValues;
    int x_c = 0;
    LineChart lineChart;


    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            /*webSocket.send("Hello, it's SSaurel !");
            webSocket.send("What's up ?");
            webSocket.send(ByteString.decodeHex("deadbeef"));
            //webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !");*/
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            text = get_temperature(text);
            output(text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("Receiving bytes : " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            output("Error : " + t.getMessage());
        }
    }

    private void start() {
        String url = "ws://sensorapiturings.herokuapp.com/echo?connectionType=client";
        String local = "ws://172.16.166.209:5000/echo?connectionType=client";
        String echo = "ws://echo.websocket.org";
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showVibrations(Integer.valueOf(txt));
            }
        });
    }

    String get_temperature(String text)
    {
        // get JSONObject from JSON file
        JSONObject obj = null;
        try {
            obj = new JSONObject(text);
            JSONObject data = obj.getJSONObject("data");
            String value = data.getString("currentHumidity");
            return value;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibrations);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        lineChart = findViewById(R.id.lineChart);
        yValues = new ArrayList<>();

        client = new OkHttpClient();
        start();
    }


    void showVibrations(int humidity)
    {
        if(!first)
        {
            lineChart.setDragEnabled(true);
            lineChart.setScaleEnabled(false);

            yValues.add(new Entry(x_c++, humidity));

            LineDataSet set1 = new LineDataSet(yValues, "Data set 1");
            LineData lineData = new LineData(set1);
            lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
            //lineChart.animateY(1000);
            lineChart.setData(lineData);
        }

        else
        {

            yValues.add(new Entry(x_c++, humidity + x_c));

            LineDataSet set1 = new LineDataSet(yValues, "Data set 1");
            LineData lineData = new LineData(set1);
            lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
            lineChart.setData(lineData);

            lineChart.notifyDataSetChanged(); // let the chart know it's data changed
            lineChart.invalidate();
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


    @Override
    protected void onPause() {
        ws.close(1000, null);
        super.onPause();
    }

    @Override
    protected void onStop() {
        ws.close(1000, null);
        super.onStop();
    }
}
