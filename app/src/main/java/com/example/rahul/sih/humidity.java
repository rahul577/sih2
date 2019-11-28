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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class humidity extends AppCompatActivity implements dialog_ota.OtaDialogListener {

    AnyChartView anyChartView;
    String url;
    Boolean first = true;
    Pie pie;
    ProgressBar progressBar;
    TextView currentTemperature, currentPpm;
    WebSocket ws;
    private OkHttpClient client;
    PieChart pieChart;
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
        String local = "ws://" + "172.16.168.45" + ":5000/echo?connectionType=client";
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
                String value = get_temperature(txt);
                if(value == null)
                    return;
                showHumidity(Integer.valueOf(value), Integer.valueOf(value), Integer.valueOf(value));
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
        setContentView(R.layout.activity_humidity);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        currentTemperature = findViewById(R.id.currentTemperature);
        currentPpm = findViewById(R.id.currentPpm);

        pieChart = findViewById(R.id.piechart);
        showHumidity(75, 0, 0);

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


    void showHumidity(int humidity, int temperature, int ppm)
    {
        if(!first)
        {
            pieChart.setUsePercentValues(true);
            pieChart.getDescription().setEnabled(false);
            pieChart.setExtraOffsets(5, 10, 5, 5);

            pieChart.setDragDecelerationFrictionCoef(0.95f);

            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColor(Color.WHITE);
            pieChart.setTransparentCircleRadius(61f);

            ArrayList<PieEntry> yValues = new ArrayList<>();

            yValues.add(new PieEntry(humidity, "Humidity"));
            yValues.add(new PieEntry(100 - humidity, ""));


            PieDataSet dataSet = new PieDataSet(yValues, "Humidity");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            final int[] JOYFUL_COLORS = {
                    Color.rgb(248, 247, 244), Color.rgb(254, 149, 7)
            };

            ArrayList<Integer> colours = new ArrayList<>();
            colours.add(Color.rgb(248, 247, 244));
            colours.add(Color.rgb(248, 247, 244));
            pieChart.setHoleColor(Color.rgb(48, 48, 48));
            dataSet.setColors(colours);

            PieData data = new PieData((dataSet));
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.YELLOW);

            pieChart.setData(data);

            first = false;

            ws.close(1000, "");
        }

        else
        {
            ArrayList<PieEntry> yValues = new ArrayList<>();

            yValues.add(new PieEntry(humidity, "Humidity"));
            yValues.add(new PieEntry(100 - humidity, ""));

            PieDataSet dataSet = new PieDataSet(yValues, "Humidity");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            ArrayList<Integer> colours = new ArrayList<>();
            colours.add(Color.rgb(77, 120, 145));
            colours.add(Color.rgb(48, 48, 48));
            colours.add(Color.rgb(48, 48, 48));
            colours.add(Color.rgb(48, 48, 48));
            colours.add(Color.rgb(48, 48, 48));
            pieChart.setHoleColor(Color.rgb(48, 48, 48));


            dataSet.setColors(colours);

            PieData data = new PieData((dataSet));
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.YELLOW);

            pieChart.setData(data);

            pieChart.notifyDataSetChanged(); // let the chart know it's data changed
            pieChart.invalidate();
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
