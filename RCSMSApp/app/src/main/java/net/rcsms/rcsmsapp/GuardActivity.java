package net.rcsms.rcsmsapp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.List;

public class GuardActivity extends AppCompatActivity {

    private Switch guard_switch;
    private Intent intentService;

    private MqttService mqttService;
    public static String MQTT_HOST = "192.168.1.17";
    private String TOPIC;

    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard);

        Intent intent = getIntent();
        deviceId = intent.getStringExtra("deviceId");
        TOPIC = deviceId + "MODE";

        mqttService = new MqttService(
                MQTT_HOST, "1883", MqttClient.generateClientId());
        mqttService.connect();

        intentService = new Intent(GuardActivity.this,
                ListenService.class);
        intentService.setPackage("net.rcsms.rcsmsapp");

        guard_switch = (Switch) findViewById(R.id.guard_switch);

        CompoundButton.OnCheckedChangeListener listener =
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {

                        if (isChecked) {
                            Log.d("==========", "start");
                            startService(intentService);
                            mqttService.publish(TOPIC, "GUARD,START");
                            MenuActivity.guardMode = true;
                        }
                        else {
                            Log.d("==========", "stop");
                            stopService(intentService);
                            mqttService.publish(TOPIC, ",END");
                            MenuActivity.guardMode = false;
                        }
                    }
                };

        guard_switch.setOnCheckedChangeListener(listener);

        guard_switch.setChecked(isRunning(ListenService.class));
    }

    private boolean isRunning(Class target) {
        boolean result = false;

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(200);
        ComponentName cn = new ComponentName(this, target);

        for (ActivityManager.RunningServiceInfo rsi : rs) {
            Log.d("===========", rsi.service.toString());
            if (rsi.service.equals(cn)) {
                result = true;
                break;
            }
        }

        return result;
    }

}
