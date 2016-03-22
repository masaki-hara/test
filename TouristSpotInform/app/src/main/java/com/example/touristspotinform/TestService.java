package com.example.touristspotinform;

/**
 * Created by Administrator on 2016/03/22.
 */
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TestService extends Service {
    @Override
    public void onCreate() {
        Log.i("TestService", "onCreate");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TestService", "onStartCommand");
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        Log.i("TestService", "onDestroy");
    }
    @Override
    public IBinder onBind(Intent arg0) {
        Log.i("TestService", "onBind");
        return null;
    }
}
