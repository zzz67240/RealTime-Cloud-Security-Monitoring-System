package net.rcsms.embedded;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import com.google.firebase.database.FirebaseDatabase;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import net.rcsms.embedded.dao.SaveRecordJdbcDao;
import net.rcsms.embedded.ic.L293D;
import net.rcsms.embedded.sensor.BalanceSensor;
import net.rcsms.embedded.sensor.DHT11Sensor;
import net.rcsms.embedded.sensor.GasSensor;
import net.rcsms.embedded.ic.MCP3008;
import net.rcsms.embedded.ic.MCP3008.MCP3008Channels;
import net.rcsms.embedded.sensor.UltraSoundSensor;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Main {

    private static final GpioController gpio = GpioFactory.getInstance();

    //超音波偵測腳位
    private static final GpioPinDigitalInput usspin1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_08);
    private static final GpioPinDigitalOutput usspin2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09);
    private static final UltraSoundSensor uss = new UltraSoundSensor(usspin1, usspin2);

    //傾倒偵測腳位
    private static final GpioPinDigitalInput bspin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_21, PinPullResistance.PULL_DOWN);
    private static final BalanceSensor bs = new BalanceSensor(bspin);

    //MCP3008腳位
    private static final GpioPinDigitalInput serialDataOutput = gpio.provisionDigitalInputPin(RaspiPin.GPIO_28);
    private static final GpioPinDigitalOutput serialDataInput = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27);
    private static final GpioPinDigitalOutput serialClock = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29);
    private static final GpioPinDigitalOutput chipSelect = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_26);
    private static final MCP3008 mcp3008 = new MCP3008(serialClock, serialDataOutput, serialDataInput, chipSelect);

    //DHT11溫溼度偵測腳位
    private static final DHT11Sensor ds = new DHT11Sensor(7);

    //瓦斯偵測腳位
    private static final GasSensor gas = new GasSensor(mcp3008, MCP3008Channels.CH_00);
    private static final GasSensor smoke = new GasSensor(mcp3008, MCP3008Channels.CH_01);

    //L293D直流馬達控制腳位
    private static final GpioPinDigitalOutput pin22 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, PinState.LOW);
    private static final GpioPinDigitalOutput pin23 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, PinState.LOW);
    private static final GpioPinDigitalOutput pin24 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, PinState.LOW);
    private static final GpioPinDigitalOutput pin25 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, PinState.LOW);
    private static final L293D l293d = new L293D(pin22, pin23, pin24, pin25);

    //蜂鳴器腳位
    private static final GpioPinDigitalOutput pin0 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW);

    private static final String FIREBASE_URL
            = "https://rcsms-2df26.firebaseio.com/";

    private static final String CHILD_TEMPERATURE = "Temperature", CHILD_HUMIDITY = "Humidity", CHILD_GAS = "Gas",
            CHILD_SMOKE = "Smoke", CHILD_WARNNING = "Warnning", CHILD_GASWARN = "GasWarn",
            CHILD_SMOKEWARN = "SmokeWarn", CHILD_FALL = "Fall";

    private static DatabaseReference firebaseRef, childTemperature, childHumidity, childGas,
            childSmoke, childWarnning, childGasWarn, childSmokeWarn, childFall;

    //設定初始值
    private static int oldTemp = 0, oldHumi = 0, oldGas = 0, oldSmok = 0;
    private static boolean oldWarn = false, oldGasW = false, oldSmokW = false, oldFall = false;

    private static final String deviceserialnumber = "D0001";

    //守衛模式的身分辨識感測距離
    private static final int guardmodedetectlimit = 30;

    private enum Mode {
        NORMAL, CONTROL, GUARD
    }

    private static Mode currentMode = Mode.NORMAL;

    private static ControlThread controlthread = null;

    private static GuardThread guardthread = null;

    //攝像頭及拍照
    private static final String WORK_DIR = "/home/pi/webcamwork";
    private static final String IMAGE_FILE
            = WORK_DIR + "/rcsms.jpg";
    private static final String TAKE_COMM
            = "fswebcam -r 640x480 --no-banner " + IMAGE_FILE;

    // MQTT broker 主機, 埠號, QOS與主題
    private static final String MQTT_HOST = "192.168.1.17";
    private static final String MQTT_PORT = "1883";
    private static final String MQTT_TOPIC_MODE = deviceserialnumber + "MODE";

    //MYSQL資料庫連線 URL
    private static final String MYSQL_HOST = "192.168.1.17";
    private static final String MYSQL_PORT = "3306";
    private static final String MYSQL_DB = "rcsms";
    private static final String MYSQL_USR = "root";
    private static final String MYSQL_PSW = "1234";
    private static final int RECORD_PERIOD = 1 * 60 * 60 * 1000;

    //NFC辨識是否通過
    private static boolean pass;

    // 儲存NFC Tag UID的檔案
    private static final String FILE_NAME = "/home/pi/nfc_uid_list.txt";

    // 所有NFC Tag UID資料
    private static final Set<String> record = new HashSet<>();

    // 開啟儲存NFC UID的檔案
    private static BufferedReader initFile() {
        File file = new File(FILE_NAME);
        BufferedReader result = null;

        try {
            result = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            System.out.println("============ " + e.toString());
        }

        return result;
    }

    // 讀取所有NFC Tag UID資料
    private static void initRecord() {
        File file = new File(FILE_NAME);

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line = null;

            while ((line = reader.readLine()) != null) {
                record.add(line);
            }
        } catch (IOException e) {
            System.out.println("============ " + e.toString());
        }
    }

    // 宣告與建立資料讀取通知物件
    private static final NfcReader.NfcCallBack nfcCallBack = (String data) -> {
        // 讀取的NFC Tag UID是否在儲存的記錄
        pass = record.contains(data);
        if (pass) {
            //pass.wav
            beep(500);
        } else {
            //wrong.wav
            beep(500);
            beep(500);
        }

        System.out.println("NFC UID: " + data
                + " [" + (pass ? "PASSED" : "LOCKED") + "]");
    };

    // 建立NFC資料讀取物件
    private static final NfcReader nfcReader = new NfcReader(nfcCallBack);

    //servo控制
    private static void set(String pin, String svalue) {
        try (OutputStream out = new FileOutputStream("/dev/servoblaster");
                OutputStreamWriter writer = new OutputStreamWriter(out)) {
            //預設servo值60為0度角，範圍為60~100，對應輸入值0~100
            int value = (int) (Integer.parseInt(svalue) * 0.4) + 60;
            writer.write(pin + "=" + value + "\n");
            writer.flush();
        } catch (IOException e) {
            System.out.println("================= " + e);
        }
    }

    private static void beep(int ms) {
        pin0.high();
        delay(ms);
        pin0.low();
        delay(30);
    }

    private static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            System.out.println(e.toString());
        }
    }

    private static void valueChangeSaveToFirebase(int value, int oldValue, DatabaseReference df) {
        if (Math.abs(value - oldValue) > 1) {
            df.setValue(value);
            oldValue = value;
        }
    }

    private static String encodeToString(BufferedImage image, String type) {
        String imageString = null;

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();
            Encoder encoder = Base64.getEncoder();
            imageString = encoder.encodeToString(imageBytes);
        } catch (IOException e) {
            System.out.println(e);
        }

        return imageString;
    }

    public static void main(String[] args) {

        //啟動用C語言編寫的servo監聽服務
        System.out.println("Start Servoblaster...");
        try {
            Runtime.getRuntime().exec("sudo /home/pi/ServoBlaster/servod --p1pins=15");
        } catch (IOException e) {
            System.out.println(e);
        }

        // 開啟儲存NFC UID的檔案 
        final BufferedReader reader = initFile();

        // 讀取所有NFC Tag UID資料
        initRecord();
        System.out.println("There are [" + record.size() + "] record(s).");

        // 應用程式結束
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("============ " + e.toString());
                }

                // 停止NFC資料讀取服務
                nfcReader.stop();

                System.out.println("NFCDemo01 Bye...");
            }
        });

        MqttService mqttService = new MqttService(MQTT_HOST, MQTT_PORT, MqttClient.generateClientId());

        // 建立與註冊 MQTT 用戶端 call back 物件
        mqttService.setCallback(new MqttCallback() {

            // 連線中斷
            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("connectionLost...");
            }

            // 接收到訊息
            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage)
                    throws Exception {
                String message = new String(mqttMessage.getPayload());
                String[] items = message.split(",");

                if (items.length == 2) {

                    //控制模式下的裝置移動操作
                    switch (items[0]) {
                        case "CONTROL":
                            switch (items[1]) {
                                case "START":
                                    if (currentMode == Mode.NORMAL) {
                                        currentMode = Mode.CONTROL;
                                        controlthread = new ControlThread();
                                        controlthread.start();
                                    }
                                    break;

                                case "END":
                                    if (currentMode == Mode.CONTROL && controlthread != null) {
                                        currentMode = Mode.NORMAL;
                                        controlthread.exit();
                                    }
                                    break;

                                case "FORWARD":
                                    l293d.forward();
                                    break;

                                case "BACKWARD":
                                    l293d.backward();
                                    break;

                                case "STOP":
                                    l293d.stop();
                                    break;

                                case "LEFT":
                                    l293d.left();
                                    break;

                                case "RIGHT":
                                    l293d.right();
                                    break;
                                default:
                                    l293d.stop();
                                    break;
                            }
                            break;
                        case "CAM":
                            set("P1-15", items[1]);
                            break;
                        case "GUARD":
                            switch (items[1]) {
                                case "START":
                                    if (currentMode == Mode.NORMAL) {
                                        currentMode = Mode.GUARD;
                                        guardthread = new GuardThread();
                                        guardthread.start();
                                    }
                                    break;

                                case "END":
                                    if (currentMode == Mode.GUARD && guardthread != null) {
                                        currentMode = Mode.NORMAL;
                                        guardthread.exit();
                                    }
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }

            // 已傳送訊息
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                System.out.println("deliveryComplete...");
            }
        });

        // 連線到 MQTT broker
        mqttService.connect();
        // 訂閱主題
        mqttService.subscribe(MQTT_TOPIC_MODE);

        InputStream input = Main.class.getResourceAsStream(
                "RCSMS-24d032cc2a9c.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(input)
                .setDatabaseUrl(FIREBASE_URL)
                .build();
        FirebaseApp.initializeApp(options);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        firebaseRef = database.getReference().child(deviceserialnumber);
        childTemperature = firebaseRef.child(CHILD_TEMPERATURE);
        childHumidity = firebaseRef.child(CHILD_HUMIDITY);
        childGas = firebaseRef.child(CHILD_GAS);
        childSmoke = firebaseRef.child(CHILD_SMOKE);
        childWarnning = firebaseRef.child(CHILD_WARNNING);
        childGasWarn = firebaseRef.child(CHILD_GASWARN);
        childSmokeWarn = firebaseRef.child(CHILD_SMOKEWARN);
        childFall = firebaseRef.child(CHILD_FALL);
        
        //警告通知初始化
        childWarnning.setValue(false);
        childGasWarn.setValue(false);
        childSmokeWarn.setValue(false);
        childFall.setValue(false);

        //攝像頭角度初始化
        set("P1-15", "0");

        RecordThread recordthread = new RecordThread();
        recordthread.start();

        SaveRecordThread saverecordthread = new SaveRecordThread();
        saverecordthread.start();

        while (true) {

            delay(500);
        }
    }

    private static class RecordThread extends Thread {

        private boolean isExit = false;

        public void exit() {
            isExit = true;
        }

        @Override
        public void run() {
            while (!isExit) {
                boolean fall = bs.fall();
                valueChangeSaveToFirebase((int) ds.readData()[0], oldTemp, childTemperature);
                valueChangeSaveToFirebase((int) ds.readData()[1], oldHumi, childHumidity);
                valueChangeSaveToFirebase(gas.detect(), oldGas, childGas);

                if (gas.detect() > 15 && oldGasW == false) {
                    childGasWarn.setValue(true);
                    oldGasW = true;
                } else if (gas.detect() < 15 && oldGasW == true) {
                    childGasWarn.setValue(false);
                    oldGasW = false;
                }

                valueChangeSaveToFirebase(smoke.detect(), oldSmok, childSmoke);

                if (smoke.detect() > 15 && oldSmokW == false) {
                    childSmokeWarn.setValue(true);
                    oldSmokW = true;
                } else if (smoke.detect() < 15 && oldSmokW == true) {
                    childSmokeWarn.setValue(false);
                    oldSmokW = false;
                }

                if (fall != oldFall) {
                    childFall.setValue(fall);
                    oldFall = fall;
                }

                Gpio.delay(500);
            }
        }
    }

    private static class SaveRecordThread extends Thread {

        private boolean isExit = false;

        public void exit() {
            isExit = true;
        }

        @Override
        public void run() {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SaveRecordJdbcDao srjd = new SaveRecordJdbcDao(MYSQL_HOST, MYSQL_PORT, MYSQL_DB, MYSQL_USR, MYSQL_PSW);

                while (!isExit) {
                    delay(RECORD_PERIOD);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    int temp = (int) ds.readData()[0];
                    int humi = (int) ds.readData()[1];
                    int gass = gas.detect();
                    int smokk = smoke.detect();
                    srjd.saveRecord(datetime, deviceserialnumber, temp, humi, gass, smokk);
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private static class ControlThread extends Thread {

        private boolean isExit = false;

        public void exit() {
            isExit = true;
        }

        @Override
        public void run() {
            // Mjpg stream
            System.out.println("Start Webcam Stream...");
            try {
                Runtime.getRuntime().exec("/home/pi/mjpgstart.sh");
            } catch (IOException e) {
                System.out.println(e);
            }

            while (!isExit) {
                boolean tooclose = uss.found(20);
                //距離過近停止前進
                if (tooclose == true) {
                    l293d.stop();
                    System.out.println("Too close!");
                }
                delay(200);
            }

            System.out.println("Stop Webcam Stream...");
            try {
                Runtime.getRuntime().exec("sudo killall mjpg_streamer");
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    private static class GuardThread extends Thread {

        private boolean isExit = false;

        public void exit() {
            isExit = true;
        }

        @Override
        public void run() {
            System.out.println("Guard Mode");
            System.out.println("NFC Start...");
            // 初始化與啟動NFC資料讀取服務
            nfcReader.init();
            while (!isExit) {
                boolean warn = uss.found(guardmodedetectlimit);
                if (warn != oldWarn) {
                    oldWarn = warn;

                    if (warn == true) {
                        System.out.println("warn == true");
                        try {
                            // 執行照相指令
                            Runtime.getRuntime().exec(TAKE_COMM);
                            // 等候完成照相指令
                            delay(2000);

                            // 讀取照片檔案
                            BufferedImage img = ImageIO.read(new File(IMAGE_FILE));
                            // 轉換照片檔為字串
                            String imgstr = encodeToString(img, "jpg");

                            // 傳送照片資料到 Firebase
                            firebaseRef.child("Image").setValue(imgstr, new CompletionListener() {

                                @Override
                                public void onComplete(DatabaseError de, DatabaseReference dr) {
                                    if (de != null) {
                                        System.out.println("Picture upload failure... " + de.getMessage());
                                    } else {
                                        System.out.println("Picture upload successfully.");
                                        childWarnning.setValue(warn);
                                    }
                                }
                            });
                        } catch (IOException e) {
                            System.out.println(e);
                        }

                        delay(500);

                        //***********播放identify.wav，進入身分辨識迴圈
                        beep(300);
                        beep(300);
                        beep(300);
                        beep(300);
                        //計算秒數用
                        int secondcount;
                        for (secondcount = 0; secondcount < 60; secondcount++) {
                            //物體遠離，則跳出迴圈
                            boolean warn2 = uss.found(guardmodedetectlimit);
                            if (warn2 == false) {
                                childWarnning.setValue(warn2);
                                oldWarn = warn2;
                                break;
                            }

                            //用NFC讀卡機，進行RFID身份辨識 
                            if (pass) {
                                delay(30 * 1000);
                                pass = false;
                                break;
                            }

                            delay(500);
                        }
                        //若身分辨識未通過，播放Warn音效，並且蜂鳴持續30秒，期間仍可進行辨識，
                        //若鳴30秒後，未辨識，回到守衛模式主迴圈
                        //***********播放warn.wav
                        if (secondcount == 60) {
                            for (int i = 0; i < 120; i++) {
                                //蜂鳴器作響
                                beep(250);
                                boolean warn3 = uss.found(guardmodedetectlimit);
                                if (warn3 == false && i >= 80) {
                                    childWarnning.setValue(warn3);
                                    oldWarn = warn3;
                                    break;
                                }
                                if (pass) {
                                    delay(30 * 1000);
                                    pass = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                delay(1000);
            }
        }
    }
}
