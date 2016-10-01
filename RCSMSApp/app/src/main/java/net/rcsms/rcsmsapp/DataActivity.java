package net.rcsms.rcsmsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataActivity extends AppCompatActivity {

    private DeviceAdapter deviceAdapter;
    private List<Device> deviceItems;

    private Firebase firebaseRef;

    private List<String> datas = Arrays.asList(
            new String[]{"Temperature", "Humidity", "Gas", "Smoke"});

    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        Intent intent = getIntent();
        deviceId = intent.getStringExtra("deviceId");

        deviceItems = new ArrayList<>();
        deviceAdapter = new DeviceAdapter(this, deviceItems);

        RecyclerView item_list = (RecyclerView) findViewById(R.id.data_list);
        item_list.addItemDecoration(new SpacesItemDecoration(12));
        item_list.setItemAnimator(null);
        item_list.setHasFixedSize(true);
        RecyclerView.LayoutManager rLayoutManager =
                new LinearLayoutManager(this);
        item_list.setLayoutManager(rLayoutManager);
        item_list.setAdapter(deviceAdapter);

        final Handler handlerDevice = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                for (Device device : deviceItems) {
                    deviceAdapter.update(device);
                }

                sendEmptyMessageDelayed(0, 2000);
            }
        };

        handlerDevice.sendEmptyMessageDelayed(0, 1000);

        firebaseRef = new Firebase("https://rcsms-2df26.firebaseio.com/");

        firebaseRef.child(deviceId).addChildEventListener(new ChildEventListenerAdapter() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();

                if (datas.contains(key)) {
                    deviceAdapter.add(new Device("S" + key, key, (Long)dataSnapshot.getValue(), true, 10, 2));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();

                if (datas.contains(key)) {
                    deviceAdapter.update(new Device("S" + key, key, (Long) dataSnapshot.getValue(), true, 10, 2));
                }
            }
        });
    }

}
