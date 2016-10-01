package net.rcsms.embedded.ic;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class L293D {

    private GpioPinDigitalOutput left01, left02, right01, right02;
    
    public L293D(GpioPinDigitalOutput left01, GpioPinDigitalOutput left02, 
            GpioPinDigitalOutput right01, GpioPinDigitalOutput right02) {
        this.left01 = left01;
        this.left02 = left02;
        this.right01 = right01;
        this.right02 = right02;
        
    }
    
    public void stop() {
        left01.low();
        left02.low();
        right01.low();
        right02.low();
    }
    
    public void forward() {
        stop();
        left01.high();
        left02.low();
        right01.high();
        right02.low();
    }
    
    public void backward(){
        stop();
        left01.low();
        left02.high();
        right01.low();
        right02.high();
    }
    
    public void left() {
        stop();
        left01.low();
        left02.high();
        right01.high();
        right02.low();
    }
    
    public void right() {
        stop();
        left01.high();
        left02.low();
        right01.low();
        right02.high();
    }
     
}
