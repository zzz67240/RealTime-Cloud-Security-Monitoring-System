package net.rcsms.rcsmsapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.utilities.Base64;

import java.io.IOException;

public class ListenService extends Service {

    private boolean isConnect = false;

    private boolean[] isFirstTime = {true, true, true, true};

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (TurtleUtil.checkNetwork(this)) {
            processListen();
        }
        else {
            Log.d("ListenService", "onStartCommand: Connection required.");
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("===========", "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ListenServiceBinder();
    }

    public class ListenServiceBinder extends Binder {
        public ListenService getListenService() {
            return ListenService.this;
        }
    }

    public void processListen() {
        if (isConnect) {
            return;
        }

        isConnect = true;

        final Firebase firebaseRef = new Firebase("https://rcsms-2df26.firebaseio.com/");

        ValueEventListener listener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot ds) {
                final String key = ds.getKey();
                final boolean result = (Boolean) ds.getValue();

                String message = "";
                boolean isNotify = false;

                switch (key) {
                    case "Fall":
                        message = "Device fall!";
                        isNotify = !isFirstTime[0];
                        isFirstTime[0] = false;
                        break;
                    case "GasWarn":
                        message = "Gas detected!";
                        isNotify = !isFirstTime[1];
                        isFirstTime[1] = false;
                        break;
                    case "SmokeWarn":
                        message = "Smoke detected!";
                        isNotify = !isFirstTime[2];
                        isFirstTime[2] = false;
                        break;
                    case "Warnning":
                        message = "Object detected!";
                        isNotify = !isFirstTime[3];
                        isFirstTime[3] = false;
                        break;
                }

                final String notifyString = message;

                if (isNotify) {
                    if (key.equals("Warnning")) {
                        Firebase imageRef = firebaseRef.child("D0001").child("Image");

                        imageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                String imageStr = (String) snapshot.getValue();

                                if (imageStr == null) {
                                    processNotify(null, key, notifyString + (result ? "" : ": relieve"));
                                    return;
                                }

                                try {
                                    byte[] imageByte = Base64.decode(imageStr);
                                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                                    processNotify(imageBitmap, key, notifyString + (result ? "" : ": relieve"));
                                }
                                catch (IOException e) {
                                    Log.d("ListenService", e.toString());
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                Log.d("ListenService", firebaseError.toString());
                            }
                        });
                    }
                    else {
                        processNotify(null, key, message + (result ? "" : ": relieve"));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError fe) {
                Log.d("ListenService", fe.toString());
            }
        };

        firebaseRef.child("D0001").child("Fall").addValueEventListener(listener);
        firebaseRef.child("D0001").child("GasWarn").addValueEventListener(listener);
        firebaseRef.child("D0001").child("SmokeWarn").addValueEventListener(listener);
        firebaseRef.child("D0001").child("Warnning").addValueEventListener(listener);
    }

    private void processNotify(Bitmap bigPicture, String key, String message) {
        if (bigPicture == null) {
            bigPicture = BitmapFactory.decodeResource(
                    getResources(), R.drawable.notify_icon);
        }

        Notification.Builder builder = new Notification.Builder(ListenService.this);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(ListenService.this, 0, new Intent(), 0);

        builder.setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setTicker(ListenService.this.getString(R.string.app_name))
                .setContentTitle(ListenService.this.getString(R.string.app_name))
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification.BigPictureStyle bigPictureStyle =
                new Notification.BigPictureStyle();
        bigPictureStyle.bigPicture(bigPicture)
                .setSummaryText(message);
        builder.setStyle(bigPictureStyle);

        NotificationManager manager =(NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        manager.notify(key, 0, notification);
    }

}
