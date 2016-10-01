package net.rcsms.embedded.sensor;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.wiringpi.Gpio;

public class BalanceSensor {
    
    private final GpioPinDigitalInput pin;
    
    public BalanceSensor(GpioPinDigitalInput pin){
        this.pin = pin;
    }
    
    public boolean fall() {
        int count = 0;
        
        for (int i = 0; i < 20; i++) {
           if  (pin.isLow()) {
               count++;
           }
           
           Gpio.delay(30);
        }
        
        return pin.isLow() && count > 10; 
    }
}
