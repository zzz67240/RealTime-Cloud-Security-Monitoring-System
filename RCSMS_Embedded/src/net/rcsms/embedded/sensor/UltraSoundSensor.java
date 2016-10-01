package net.rcsms.embedded.sensor;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class UltraSoundSensor {
    
    private final GpioPinDigitalOutput pinTrig;
    private final GpioPinDigitalInput pinEcho;
    
    public UltraSoundSensor(GpioPinDigitalInput pinEcho, GpioPinDigitalOutput pinTrig){
        this.pinEcho = pinEcho;
        this.pinTrig = pinTrig;
    }
 
    public boolean found(int limit) {
        float distance = detect();
        return distance >= 0 && distance < limit;
    }
    
    // 觸發超音波感應器
    private void trigger() {
        // 設定為高電壓
        pinTrig.setState(true);
        // 維持0.00001秒、10000奈秒
        delay(0, 10000);
        // 設定為低電壓
        pinTrig.setState(false);
    }
    
    // 判斷超音波感應器是否回應高電壓，表示感應器發射超音波
    private boolean echoHigh() {
        // 測試五千次
        for (int i = 0; i < 5000; i++) {
            // 如果是高電壓
            if (pinEcho.isHigh()) {
                return true;
            }
        }
        
        return false;
    }
    
    // 判斷超音波感應器是否回應低電壓，表示感應器接收到傳回的超音波
    private boolean echoLow() {
        // 測試五千次
        for (int i = 0; i < 5000; i++) {
            // 如果是低電壓
            if (pinEcho.isLow()) {
                return true;
            }
        }
        
        return false;
    }
    
    // 使用超音波感應器傳回測試距離
    public float detect() {
        // 送出觸發訊號給超音波測距模組
        trigger();
        
        // 判斷超音波感應器是否回應高電壓
        if (!echoHigh()) {
            return -1;
        }
        
        // 記錄時間（奈秒）
        long start = System.nanoTime();
        
        // 判斷超音波感應器是否回應低電壓
        if (!echoLow()) {
            return -1;
        }
        
        // 記錄時間（奈秒）
        long end = System.nanoTime();
        
        // 計算超音波發射與接收的時間（奈秒）
        long pulse = end - start;
        
        // 計算距離（公分）
        float result = pulse / 1_000_000_000F;
        result = result * 340 * 100 / 2;
        
        return result; 
    }
    
    // 暫停指定的時間（毫秒、1000分之一秒）    
    public static void delay(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
            System.out.println("============ " + e.toString());
        }        
    }
    
    // 暫停指定的時間（毫秒，奈秒）       
    public static void delay(int ms, int ns) {
        try {
            Thread.sleep(ms, ns);
        }
        catch (InterruptedException e) {
            System.out.println("============ " + e.toString());
        }        
    }
    
}
