package com.example.rahul.sih;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.anastr.speedviewlib.SpeedView;
import com.iammert.library.readablebottombar.ReadableBottomBar;
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
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class pressure extends AppCompatActivity implements ExampleDialog.ExampleDialogListener, PopupMenu.OnMenuItemClickListener, dialog_ota.OtaDialogListener, TextToSpeech.OnInitListener {

    ProgressBar progressBar;
    WebSocket ws;
    private OkHttpClient client;
    SpeedView speedometer;
    PopupMenu popup;
    List list;
    String str = "";
    int current = 1;
    private TextToSpeech tts;
    private BoomMenuButton bmb;
    private final ArrayList<Pair> piecesAndButtons = new ArrayList<>();

    void add_index(int idx)
    {
        if(list.contains(idx))
            return;

        list.add(idx);
        showPopup(findViewById(R.id.relativeLayout));
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
        String local = "ws://" + "172.16.168.45" + ":5000/echo?connectionType=client";
        Toast.makeText(this, local, Toast.LENGTH_SHORT).show();
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
                List<String> list;
                list = get_temperature(txt);
                if(list == null)
                    return;
                add_index(Integer.parseInt(list.get(0)));
                str += list.get(0);
                if(current == Integer.parseInt(list.get(0)))
                    speedometer.speedTo(Float.parseFloat(list.get(1)));
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
            list.add(data.getString("currentPressure"));
            return list;

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
        progressBar.setVisibility(View.INVISIBLE);

        speedometer = findViewById(R.id.speedView);

        speedometer.setWithTremble(false);

        speedometer.setMaxSpeed(2);
        speedometer.setMinSpeed(0);

        list = new ArrayList<Integer>();
        showPopup(findViewById(R.id.relativeLayout));
        tts = new TextToSpeech(this, this);

        client = new OkHttpClient();
        start();

        speedometer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tts.setLanguage(Locale.US);
                tts.speak("Danger ahead", TextToSpeech.QUEUE_ADD, null);
                openDialog();
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

    public void showPopup(View v) {
        popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);

        int idx = 0;
        for (int i = 0; i < list.size(); i++) {
            popup.getMenu().add(0, 0, idx++, "item" + String.valueOf( list.get(i) ));
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String str = String.valueOf(item.getTitle());
        str = str.substring(4);
        current = Integer.parseInt(str);
        return true;
    }

    public void openHumidity(View v) {
        Intent intent = new Intent(getApplicationContext(), humidity.class);
        startActivity(intent);
    }


    public void openTemperature(View v) {
        Intent intent = new Intent(getApplicationContext(), thermometer.class);
        startActivity(intent);
    }


    public void openPressure(View v) {
        popup.show();
    }
    public void openVibrations(View v) {
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


    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

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