package net.rcsms.rcsmsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.SeekBar;

import org.eclipse.paho.client.mqttv3.MqttClient;

public class ControlActivity extends AppCompatActivity {

    private JoyStickView joy_stick_view;
    private SeekBar webcam_control;
    private WebView webview;
    private MqttService mqttService;

    public static String WEBCAM_IP = "192.168.1.241";
    public static String MQTT_HOST = "192.168.1.17";
    private String TOPIC;

    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        Intent intent = getIntent();
        deviceId = intent.getStringExtra("deviceId");
        TOPIC = deviceId + "MODE";

        mqttService = new MqttService(
                MQTT_HOST, "1883", MqttClient.generateClientId());
        mqttService.connect();

        joy_stick_view = (JoyStickView) findViewById(R.id.joy_stick_view);
        webcam_control = (SeekBar) findViewById(R.id.webcam_control);

        JoyStickView.CallBack callBack = new JoyStickView.CallBack() {

            @Override
            public void control(ControlType action) {
                String content = String.valueOf(action.getCode());
                String message = "STOP";

                switch (content) {
                    case "F":
                        message = "FORWARD";
                        break;
                    case "B":
                        message = "BACKWARD";
                        break;
                    case "L":
                        message = "LEFT";
                        break;
                    case "R":
                        message = "RIGHT";
                        break;
                }

                mqttService.publish(TOPIC, "CONTROL," + message);
            }

        };

        webcam_control.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    private int oldValue = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        int value = seekBar.getProgress() / 10;

                        if (oldValue != value) {
                            mqttService.publish(TOPIC, "CAM," + value * 10);
                            oldValue = value;
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) { }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) { }
                });

        joy_stick_view.setCallBack(callBack);
        joy_stick_view.setEnabled(false);

        webview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        joy_stick_view.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mqttService.publish(TOPIC, "CONTROL,START");
        webview.loadUrl("http://" + WEBCAM_IP + ":8080/javascript_simple.html");
    }

    @Override
    protected void onPause() {
        mqttService.publish(TOPIC, "CONTROL,END");
        super.onPause();
    }

}
