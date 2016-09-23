package net.rcsms.domain;

import java.io.Serializable;
import java.util.Date;

public class Device implements Serializable {
    
    private String serialnumber;
    private int devicetype;
    private String sdevicetype;
    private Date productiondate;
    private Date purchasedate;
    private Date warrantydate;
    
    public Device(){
        
    }

    public Device(String serialnumber, int devicetype, Date productiondate, Date purchasedate, Date warrantydate) {
        this.serialnumber = serialnumber;
        this.devicetype = devicetype;
        this.productiondate = productiondate;
        this.purchasedate = purchasedate;
        this.warrantydate = warrantydate;
    }
    
    public Device(String serialnumber, String sdevicetype, Date productiondate, Date purchasedate, Date warrantydate) {
        this.serialnumber = serialnumber;
        this.sdevicetype = sdevicetype;
        this.productiondate = productiondate;
        this.purchasedate = purchasedate;
        this.warrantydate = warrantydate;
    }

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }

    public int getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(int devicetype) {
        this.devicetype = devicetype;
    }

    public String getSdevicetype() {
        return sdevicetype;
    }

    public void setSdevicetype(String sdevicetype) {
        this.sdevicetype = sdevicetype;
    }

    public Date getProductiondate() {
        return productiondate;
    }

    public void setProductiondate(Date productiondate) {
        this.productiondate = productiondate;
    }

    public Date getPurchasedate() {
        return purchasedate;
    }

    public void setPurchasedate(Date purchasedate) {
        this.purchasedate = purchasedate;
    }

    public Date getWarrantydate() {
        return warrantydate;
    }

    public void setWarrantydate(Date warrantydate) {
        this.warrantydate = warrantydate;
    }
    
    @Override
    public String toString(){
        return serialnumber + ":" + devicetype + ":" + productiondate + ":" + purchasedate + ":" + warrantydate;
    }
}
