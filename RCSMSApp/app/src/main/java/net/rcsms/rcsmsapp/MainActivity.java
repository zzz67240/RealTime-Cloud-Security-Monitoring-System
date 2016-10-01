package net.rcsms.rcsmsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText customerid, password;
    // 登入中與登入表單元件
    private View login_container;
    // 執行登入工作的執行緒
    private UserLoginTask userLoginTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        checkNetwork();
        processViews();
    }

    public void clickConnect(View view) {
        processLogin();
    }

    private void checkNetwork() {
        if (!TurtleUtil.checkNetwork(this)) {
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setMessage(R.string.connection_require);
            ab.setTitle(android.R.string.dialog_alert_title);
            ab.setIcon(android.R.drawable.ic_dialog_alert);
            ab.setCancelable(false);
            ab.setPositiveButton(getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            ab.show();
        }
    }

    private void processViews() {
        login_container = findViewById(R.id.login_form);

        customerid = (EditText)findViewById(R.id.customerid);
        password = (EditText)findViewById(R.id.password);

        customerid.setText(TurtleUtil.getPref(this, TurtleUtil.KEY_CUSTOMERID, "10"));
        password.setText(TurtleUtil.getPref(this, TurtleUtil.KEY_PASSWORD, "12345678"));
    }

    // 執行登入
    private void processLogin() {
        hideSoftKeyboard(this);

        customerid.setError(null);
        password.setError(null);

        String customeridValue = customerid.getText().toString();
        String passwordValue = password.getText().toString();

        View focusView = null;

        // 檢查輸入的內容
        focusView = checkEmpty(customerid, customeridValue, "This field is required");

        if (focusView == null) {
            focusView = checkEmpty(password, passwordValue, "This field is required");
        }

        // 如果輸入的內容有錯誤
        if (focusView != null) {
            focusView.requestFocus();
        } else {
            TurtleUtil.savePref(this, TurtleUtil.KEY_CUSTOMERID, customeridValue);
            TurtleUtil.savePref(this, TurtleUtil.KEY_PASSWORD, passwordValue);

            // 建立與啟動執行登入工作的執行緒
            userLoginTask = new UserLoginTask(customeridValue, passwordValue);
            userLoginTask.execute();
        }
    }

    private View checkEmpty(EditText et, String value, String message) {
        View result = null;

        if (TextUtils.isEmpty(value)) {
            et.setError(message);
            result = et;
        }

        return result;
    }

    // 執行登入工作的執行緒類別
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String customeridValue;
        private final String passwordValue;

        UserLoginTask(String customeridValue, String passwordValue) {
            this.customeridValue = customeridValue;
            this.passwordValue = passwordValue;
        }

        @Override
        protected String doInBackground(Void... params) {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("customerserialnumber", customeridValue);
            String url = HttpClientUtil.MOBILE_URL + "device.get";
            String result = HttpClientUtil.sendPost(url, parameters);
            return result;
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                final String[] devices = result.split(",");

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Select Device").setCancelable(false);
                dialog.setItems(devices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                        intent.putExtra("deviceId", devices[which]);
                        startActivity(intent);
                    }
                });

                dialog.show();
            }
            else {
                password.setError("This password is incorrect");
                password.requestFocus();
            }

        }

        @Override
        protected void onCancelled() {
            userLoginTask = null;
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


////    private static final String GET_DEVICE_URL =
////            "http://10.0.1.15:8080/RCSMS_Web/mobile/device.get";
//
//    public static final int QOS = 2;
//    public static final int TIMEOUT = 3;
//    private static MqttClient mqttClient;
//
//    private TextView info;
//    private EditText csn;
//
//    private static final String FIREBASE_URL =
//            "https://rcsms-e1626.firebaseio.com";
//    private Firebase firebaseRef, deviceRef;
//    public static final String CHILD_DEVICE = "D0001";
//
//    private MqttService ms;
//
////    private Firebase firebaseRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
//
//        info = (TextView) findViewById(R.id.info);
//        csn = (EditText) findViewById(R.id.csn);
//
//        firebaseRef = new Firebase(FIREBASE_URL);
//        deviceRef = firebaseRef.child("D0001");
//
//        deviceRef.addChildEventListener(new ChildEventListenerAdapter() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                String key = dataSnapshot.getKey();
//                Object value = dataSnapshot.getValue();
//                info.setText(key + "-" + value);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                String key = dataSnapshot.getKey();
//                Object value = dataSnapshot.getValue();
//                info.setText(key + "-" + value);
//            }
//        });
//
//        ms = new MqttService(
//                "10.0.1.5", "1883", MqttClient.generateClientId());
//        ms.connect();
//
//        SeekBar webcam = (SeekBar) findViewById(R.id.webcam);
//        webcam.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                //info.setText("onStartTrackingTouch: " + seekBar.getProgress());
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                //info.setText("onStopTrackingTouch: " + seekBar.getProgress());
//                ms.publish("D0001RobotControl", "CAM," + seekBar.getProgress());
//            }
//        });
//    }
//
//    public void getDevices(View view) {
//        String csnValue = csn.getText().toString();
//        Map<String, String> parameters = new HashMap<String, String>();
//        parameters.put("customerserialnumber", csnValue);
//        String url = HttpClientUtil.MOBILE_URL + "device.get";
//        String result = HttpClientUtil.sendPost(url, parameters);
//        info.setText(result);
//    }
//
//    public void control(View view) {
//        int id = view.getId();
//
//        String message = "";
//
//        switch (id) {
//            case R.id.btn_start:
//                message += "START";
//                break;
//            case R.id.btn_end:
//                message += "END";
//                break;
//            case R.id.btn_forward:
//                message += "FORWARD";
//                break;
//            case R.id.btn_back:
//                message += "BACK";
//                break;
//            case R.id.btn_left:
//                message += "LEFT";
//                break;
//            case R.id.btn_right:
//                message += "RIGHT";
//                break;
//            case R.id.btn_stop:
//                message += "STOP";
//                break;
//            // "CAM,nnn"
//        }
//
//        ms.publish("D0001RobotControl", message);
//        ms.publish("D0001RobotGuard", "START");
//    }

}
