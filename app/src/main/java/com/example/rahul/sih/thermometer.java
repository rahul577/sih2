package com.example.rahul.sih;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.SingleValueDataSet;
import com.anychart.charts.LinearGauge;
import com.anychart.enums.Anchor;
import com.anychart.enums.Layout;
import com.anychart.enums.Orientation;
import com.anychart.enums.Position;
import com.anychart.scales.Base;
import com.anychart.scales.Linear;
import com.iammert.library.readablebottombar.ReadableBottomBar;
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

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class thermometer extends AppCompatActivity implements ExampleDialog.ExampleDialogListener, PopupMenu.OnMenuItemClickListener,dialog_ota.OtaDialogListener{

    AnyChartView anyChartView;
    Boolean first = true;
    LinearGauge linearGauge;
    ProgressBar progressBar;
    WebSocket ws;
    private OkHttpClient client;
    PopupMenu popup;
    int current = 1;
    String str = "";
    List list;
    private BoomMenuButton bmb;
    private final ArrayList<Pair> piecesAndButtons = new ArrayList<>();


    void add_index(int idx)
    {
        if(list.contains(idx))
            return;

        list.add(idx);
        showPopup(findViewById(R.id.relativeLayout));
    }

    public void showPopup(View v) {
        popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);

        int idx = 0;
        for (int i = 0; i < list.size(); i++) {
            popup.getMenu().add(0, 0, idx++, "item" + String.valueOf( list.get(i) ));
        }
    }


    private final class EchoWebSocketListener extends WebSocketListener  {
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
        //ws.send("message");
    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(txt == null)
                    return;
                List<String> list;
                list = get_temperature(txt);
                if(list == null)
                    return;
                add_index(Integer.parseInt(list.get(0)));
                str += list.get(0);
                if(current == Integer.parseInt(list.get(0)))
                    showTemperature(Integer.parseInt(list.get(1)));
            }
        });
    }

    List<String> get_temperature(String text)
    {
        // get JSONObject from JSON file
        JSONObject obj = null;
        try {
            List<String> list = new ArrayList<String>();
            obj = new JSONObject(text);
            JSONObject data = obj.getJSONObject("data");
            list.add(obj.getString("sensorIndex"));
            //add new sensor
            list.add(data.getString("currentTemperature"));
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
        setContentView(R.layout.activity_thermometer);
        anyChartView = findViewById(R.id.anychart_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        list = new ArrayList<Integer>();
        showPopup(findViewById(R.id.relativeLayout));

        showTemperature(50);
        client = new OkHttpClient();
        start();

        anyChartView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                openDialog();
                Toast.makeText(getApplicationContext(), "Long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

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
            builder.listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
                    openOtaDialog();
                }
            });
            bmb.addBuilder(builder);
        }
    }


    void showTemperature(int temp)
    {
        progressBar.setVisibility(View.INVISIBLE);
        if(!first)
        {
            //APIlib.getInstance().setActiveAnyChartView(anyChartView);
            linearGauge.data(new SingleValueDataSet(new Integer[] { temp }));
        }
        else
        {
            linearGauge = AnyChart.linear();
            linearGauge.background().fill("#303030");


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
                    .fontColor("white")
                    .fontSize(17);

            linearGauge.label(1)
                    .useHtml(true)
                    .text("F&deg;")
                    .position(Position.RIGHT_BOTTOM)
                    .anchor(Anchor.RIGHT_BOTTOM)
                    .offsetY("20px")
                    .offsetX("47.5%")
                    .fontColor("white")
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
                                    "    return '<span style=\"color:white;\">' + this.value + '&deg;</span>'\n" +
                                    "  }")
                    .useHtml(true);

            linearGauge.axis(1).minorTicks(true);
            linearGauge.axis(1).labels()
                    .format(
                            "function () {\n" +
                                    "    return '<span style=\"color:white;\">' + this.value + '&deg;</span>'\n" +
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

            anyChartView.setLicenceKey("bipinkumar919@gmail.com-a1db75f-64d914b5");
            linearGauge.credits().enabled(false);

            first = false;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String str = String.valueOf(item.getTitle());
        str = str.substring(4);
        current = Integer.parseInt(str);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        return true;
    }

    public void openHumidity(View v) {
        Intent intent = new Intent(getApplicationContext(), humidity.class);
        startActivity(intent);
    }


    public void openTemperature(View v) {
        Toast.makeText(getApplication(), String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
        popup.show();
    }


    public void openPressure(View v) {
        Intent intent = new Intent(getApplicationContext(), pressure.class);
        startActivity(intent);
    }

    public void openVibrations(View v) {
        Intent intent = new Intent(getApplicationContext(), vibrations.class);
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        if(anyChartView != null)
        {
            ((ViewGroup) anyChartView.getParent()).removeView(anyChartView);
        }
        if(ws != null)
            ws.close(1000, "");
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(ws != null)
            ws.close(1000, "");
        super.onStop();
    }

    void openDialog()
    {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String username, String password) {
        Toast.makeText(this, username + password, Toast.LENGTH_SHORT).show();
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
}
