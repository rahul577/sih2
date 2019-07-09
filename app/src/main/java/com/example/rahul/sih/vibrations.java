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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class vibrations extends AppCompatActivity {


    AnyChartView anyChartView;
    RequestQueue requestQueue;
    String url;
    Boolean first = true;
    ProgressBar progressBar;
    int xcoordinate;
    List<DataEntry> seriesData = new ArrayList<>();
    Set set;
    Cartesian cartesian;
    Mapping series1Mapping, series2Mapping, series3Mapping;
    int year = 0;
    WebSocket ws;
    private OkHttpClient client;


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
                showVibrations(Double.valueOf(txt), Double.valueOf(txt), Double.valueOf(txt));
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
        anyChartView = findViewById(R.id.anychart_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        anyChartView = findViewById(R.id.anychart_view);


        client = new OkHttpClient();
        start();
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
                    // TODO ystroke
                    .yStroke((Stroke) null, null, null, (String) null, (String) null);

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

            cartesian.title("Trend of Sales of the Most Popular Products of ACME Corp.");

            cartesian.yAxis(0).title("Number of Bottles Sold (thousands)");
            cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

            //List<DataEntry> seriesData = new ArrayList<>();
            seriesData.add(new CustomDataEntry(String.valueOf(year++), x, y, z));

            set = Set.instantiate();
            set.data(seriesData);
            series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
            series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
            series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

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
            seriesData.add(new CustomDataEntry(String.valueOf(year++), x, y, z));
            set.data(seriesData);

            series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
            series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
            series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

        }
    }

    public void change(View view) {
        showVibrations(1.0, 1.5, 2.9);
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


    @Override
    protected void onPause() {
        ((ViewGroup) anyChartView.getParent()).removeView(anyChartView);
        ws.close(1000, null);
        super.onPause();
    }

    @Override
    protected void onStop() {
        ws.close(1000, null);
        super.onStop();
    }
}
