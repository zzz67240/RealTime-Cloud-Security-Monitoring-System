package net.rcsms.embedded.sensor;

import net.macdidi5.rpi.sensor.dht11.DHT11SensorReader;


public class DHT11Sensor {
    
    private final int DTHPIN;
    
    public DHT11Sensor(int DTHPIN){
        this.DTHPIN = DTHPIN;
        // 載入使用 C 撰寫的 DHT11 模組
        System.loadLibrary("dht11sensor");
    }
    
    // 讀取溫、濕度
    //    int pin   DHT11 連接的針腳編號    
    public float[] readData() {
        float[] data = DHT11SensorReader.readData(DTHPIN);
        int stopCounter = 0;
        
        while (!isValid(data)) {
            stopCounter++;
            
            if (stopCounter > 10) {
                System.out.println("Invalid data:" + data[0] + ", " + data[1]);
            }
            
            data = DHT11SensorReader.readData(DTHPIN);
        }
        
        return data;
    }
    
    // 檢查讀取的溫、濕度資料是否正確
    private static boolean isValid(float[] data) {
        return data[0] > 0 && data[0] < 100 && data[1] > 0 && data[1] < 100;
    }
}
