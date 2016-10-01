package net.rcsms.rcsmsapp;

import android.app.Application;

import com.firebase.client.Firebase;

public class MyApplication extends Application {
// for API 16 ~ 19, add 'multiDexEnabled true' in build.gradle too
//public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

}
