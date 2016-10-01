package net.rcsms.domain;

import java.io.Serializable;
import java.util.Date;

public class Record implements Serializable {
    
    private int serialnumber;
    private String datetime;
    private String deviceserialnumber;
    private int temperature;
    private int humidity;
    private int gas;
    private int smoke;
    
    public Record(){
        
    }

    public Record(int serialnumber, String datetime, String deviceserialnumber, int temperature, int humidity, int gas, int smoke) {
        this.serialnumber = serialnumber;
        this.datetime = datetime;
        this.deviceserialnumber = deviceserialnumber;
        this.temperature = temperature;
        this.humidity = humidity;
        this.gas = gas;
        this.smoke = smoke;
    }

    public int getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(int serialnumber) {
        this.serialnumber = serialnumber;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getDeviceserialnumber() {
        return deviceserialnumber;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getGas() {
        return gas;
    }

    public int getSmoke() {
        return smoke;
    }
    
    @Override
    public String toString(){
        return serialnumber + ":" + datetime + ":" + deviceserialnumber + ":" + temperature + ":" + humidity + ":" + gas + ":" + smoke;
    }
}
