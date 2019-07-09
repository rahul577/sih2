package com.example.rahul.sih;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import com.anychart.graphics.vector.SolidFill;
import com.anychart.graphics.vector.text.HAlign;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class pressure extends AppCompatActivity {

    AnyChartView anyChartView;
    String url;
    Boolean first = true;
    CircularGauge circularGauge;
    ProgressBar progressBar;
    WebSocket ws;
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private OkHttpClient client;


    void close()
    {
        ws.close(NORMAL_CLOSURE_STATUS, "Goodbye !");
    }



    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            webSocket.send("Hello, it's SSaurel !");
            webSocket.send("What's up ?");
            webSocket.send(ByteString.decodeHex("deadbeef"));
            //webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !");
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
            //output("Closing : " + code + " / " + reason);
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
                Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT).show();
                showPressure(Double.valueOf(txt));
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







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        circularGauge = AnyChart.circular();
        progressBar.setVisibility(View.INVISIBLE);
        anyChartView = findViewById(R.id.anychart_view);

        client = new OkHttpClient();
        start();

    }

    void showPressure(double pressure)
    {
        Toast.makeText(this, String.valueOf(pressure), Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.INVISIBLE);
        if(first)
        {
            circularGauge = AnyChart.circular();

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
                    .minimum(0)
                    .maximum(140);

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
                    .text("<span style=\"font-size: 25\">Wind Speed</span>")
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
            circularGauge.label(1)
                    .text("<span style=\"font-size: 20\">" + pressure + "</span>")
                    .useHtml(true)
                    .hAlign(HAlign.CENTER);
            circularGauge.label(1)
                    .anchor(Anchor.CENTER_TOP)
                    .offsetY(-100)
                    .padding(5, 10, 0, 0)
                    .background("{fill: 'none', stroke: '#c1c1c1', corners: 3, cornerType: 'ROUND'}");
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
        if(anyChartView != null)
        {
            ((ViewGroup) anyChartView.getParent()).removeView(anyChartView);
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        if(ws != null)
            ws.close(1000, "");
        super.onStop();
    }
}