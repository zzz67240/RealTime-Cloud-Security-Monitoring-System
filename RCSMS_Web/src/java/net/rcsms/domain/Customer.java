package net.rcsms.domain;

import java.io.Serializable;
import java.util.Date;

public class Customer implements Serializable {
    
    private int serialnumber;
    private String firstname;
    private String lastname;
    private int gender;
    private Date birthday;
    private String phonenumber;
    private String address;
    
    public Customer(){
        
    }

    public Customer(int serialnumber, String firstname, String lastname, int gender, Date birthday, String phonenumber, String address) {
        this.serialnumber = serialnumber;
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.birthday = birthday;
        this.phonenumber = phonenumber;
        this.address = address;
    }

    public int getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(int serialnumber) {
        this.serialnumber = serialnumber;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString(){
        return serialnumber + ":" + firstname + ":" + lastname + ":" + gender + ":" + birthday + ":" + phonenumber + ":" + address;
    }
}
