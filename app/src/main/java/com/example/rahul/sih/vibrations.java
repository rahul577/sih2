package com.example.rahul.sih;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
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
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class vibrations extends AppCompatActivity implements dialog_ota.OtaDialogListener{

    Boolean first = true;
    ProgressBar progressBar;
    List<DataEntry> seriesData = new ArrayList<>();
    List<Float> l = new ArrayList<>();
    WebSocket ws;
    private OkHttpClient client;
    int val = 0;
    int x_c = 0;
    LineChart lineChart;
    private BoomMenuButton bmb;
    private final ArrayList<Pair> piecesAndButtons = new ArrayList<>();


    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
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
        String ip = getResources().getString(R.string.ip);

        //String local = "ws://" + ip  + ":5000/echo?connectionType=client";
        String local = "ws://" + "172.16.0168.45" + ":5000/echo?connectionType=client";
        //Toast.makeText(this, local, Toast.LENGTH_SHORT).show();
        String echo = "ws://echo.websocket.org";
        okhttp3.Request request = new okhttp3.Request.Builder().url(local).build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(txt == null)
                    return;
                List<Float> list;
                list = get_temperature(txt);
                if(list == null)
                    return;
                showVibrations(list);
            }
        });
    }

    List<Float> get_temperature(String text)
    {
        // get JSONObject from JSON file
        JSONObject obj = null;
        try {
            List<Float> list = new ArrayList<Float>();
            obj = new JSONObject(text);
            JSONObject data = obj.getJSONObject("data");
            JSONArray arr = data.getJSONArray("currentVibration");

            for(int i=0; i<arr.length(); i++){
                String value = arr.getString(i);
                list.add(Float.parseFloat(value));
            }

            return list;

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

        client = new OkHttpClient();
        start();

        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_1);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_1);
        bmb.addBuilder(BuilderManager.getHamButtonBuilder());

        BuilderManager.getHamButtonData(piecesAndButtons);
        int position = 12;
        bmb.setPiecePlaceEnum((PiecePlaceEnum) piecesAndButtons.get(position).first);
        bmb.setButtonPlaceEnum((ButtonPlaceEnum) piecesAndButtons.get(position).second);
        bmb.clearBuilders();
        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++)
        {
            HamButton.Builder builder = BuilderManager.getHamButtonBuilder();

            switch (i)
            {
                case 0:
                    builder.normalTextRes(R.string.sensor0);
                    builder.subNormalTextRes(R.string.sensorName0);
                    break;
                case 1:
                    builder.normalTextRes(R.string.sensor1);
                    builder.subNormalTextRes(R.string.sensorName1);
                    break;
                case 2:
                    builder.normalTextRes(R.string.sensor2);
                    builder.subNormalTextRes(R.string.sensorName2);
                    break;
                case 3:
                    builder.normalTextRes(R.string.sensor3);
                    builder.subNormalTextRes(R.string.sensorName3);
                    break;
                case 4:
                    builder.normalTextRes(R.string.sensor4);
                    builder.subNormalTextRes(R.string.sensorName4);
                    break;
                default:
                    builder.normalTextRes(R.string.sensor0);
                    builder.subNormalTextRes(R.string.sensorName0);
                    break;
            }


            builder.listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
                    openOtaDialog();
                }
            });
            bmb.addBuilder(builder);
        }
    }


    void showVibrations(List<Float> list)
    {
        if(!first)
        {
            ArrayList<Entry> yValues = new ArrayList<>();
            lineChart.setDragEnabled(true);
            lineChart.setScaleEnabled(false);

            Integer x_c = 0;

            for(int i = 0; i < list.size(); i++)
                yValues.add(new Entry(x_c++, list.get(i)));

            LineDataSet set1 = new LineDataSet(yValues, "Data set 1");
            LineData lineData = new LineData(set1);
            lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
            lineChart.getXAxis().setTextColor(Color.rgb(255,255,255));
            lineChart.getAxisLeft().setTextColor(Color.rgb(255,255,255));
            //lineChart.animateY(1000);
            lineChart.setData(lineData);
        }

        else
        {
            ArrayList<Entry> yValues = new ArrayList<>();
            Integer x_c = 0;

            for(int i = 0; i < list.size(); i++)
                yValues.add(new Entry(x_c++, list.get(i)));

            LineDataSet set1 = new LineDataSet(yValues, "Data set 1");
            LineData lineData = new LineData(set1);
            lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
            lineChart.setData(lineData);
            lineChart.getXAxis().setTextColor(Color.rgb(255,255,255));
            lineChart.getAxisLeft().setTextColor(Color.rgb(255,255,255));


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
        if(ws != null)
            ws.close(1000, "");
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(ws != null)
            ws.close(1000, null);
        super.onStop();
    }

    //

    public void openOtaDialog() {
        dialog_ota dialog = new dialog_ota();
        dialog.show(getSupportFragmentManager(), "Ota dialog");
    }

    @Override
    public void applyOtaTexts(String ip) {
        Toast.makeText(this, ip, Toast.LENGTH_SHORT).show();
    }

    //

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
        /*if (id == R.id.mybutton) {
            // do something here
        }*/
        return super.onOptionsItemSelected(item);
    }

    //
}
