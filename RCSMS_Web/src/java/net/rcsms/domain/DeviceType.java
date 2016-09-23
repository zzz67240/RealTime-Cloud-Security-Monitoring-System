package net.rcsms.domain;

import java.io.Serializable;

public class DeviceType implements Serializable {
    
    private int serialnumber;
    private String type;
    
    public DeviceType(){
        
    }

    public DeviceType(int serialnumber, String type) {
        this.serialnumber = serialnumber;
        this.type = type;
    }

    public int getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(int serialnumber) {
        this.serialnumber = serialnumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString(){
        return serialnumber + ":" + type;
    }
}
