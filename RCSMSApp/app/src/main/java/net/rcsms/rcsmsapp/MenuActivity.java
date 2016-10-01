package net.rcsms.rcsmsapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuActivity extends AppCompatActivity {

    private String[] menuItems =
            { "Remote control", "Guard mode", "Realtime data" };
    private int[] menuIcons = { R.drawable.menu_control,
            R.drawable.menu_guard,
            R.drawable.menu_data };
    private ListView menu;
    private TextView info;
    private String deviceId;

    public static boolean guardMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        processViews();
        processControllers();

        Intent intent = getIntent();
        deviceId = intent.getStringExtra("deviceId");
        info.setText("Device ID: " + deviceId);
    }

    private void processViews() {
        menu = (ListView) findViewById(R.id.menu);
        info = (TextView) findViewById(R.id.info);

        // 建立選單資料物件
        ArrayList<HashMap<String, Object>> myList =
                new ArrayList<HashMap<String, Object>>();

        // 設定選單資料物件
        for (int i = 0; i < menuItems.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("main_menu_image", menuIcons[i]);
            map.put("main_menu_title", menuItems[i]);
            myList.add(map);
        }

        // 建立選單元件使用的Adapter物件
        SimpleAdapter sa = new SimpleAdapter(this, myList,
                R.layout.main_menu_view, new String[] { "main_menu_image",
                "main_menu_title" }, new int[] {
                R.id.main_menu_image, R.id.main_menu_title });

        // 設定選單元件使用的Adapter物件
        menu.setAdapter(sa);
    }

    private void processControllers() {
        menu.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent();

                switch (position) {
                    case 0:
                        if (MenuActivity.guardMode) {
                            AlertDialog.Builder ab = new AlertDialog.Builder(MenuActivity.this);
                            ab.setMessage("Guard Mode");
                            ab.setTitle(android.R.string.dialog_alert_title);
                            ab.setIcon(android.R.drawable.ic_dialog_alert);
                            ab.setCancelable(false);
                            ab.setPositiveButton(getString(android.R.string.ok), null);

                            ab.show();
                            return;
                        }

                        intent.setClass(MenuActivity.this,
                                ControlActivity.class);
                        intent.putExtra("deviceId", deviceId);
                        startActivity(intent);
                        break;
                    case 1:
                        intent.setClass(MenuActivity.this,
                                GuardActivity.class);
                        intent.putExtra("deviceId", deviceId);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(MenuActivity.this,
                                DataActivity.class);
                        intent.putExtra("deviceId", deviceId);
                        startActivity(intent);
                        break;
                }
            }
        });
    }
}
