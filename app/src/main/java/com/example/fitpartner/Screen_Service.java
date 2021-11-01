package com.example.fitpartner;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class Screen_Service extends Service {

    private Screen_Recevier mReceiver = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("111", "onCreate: 스크린서비스");
        mReceiver = new Screen_Recevier();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mReceiver,filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {  //서비스를 강제 종료했을때, 서비스를 어떤 방법으로 다시 시작시킬지 결정.
        super.onStartCommand(intent, flags, startId);
        Log.d("111", "onStartCommand: 서비스스크린 ");
        if(intent != null){
            Log.d("111", "onStartCommand: 설마여기");
            if(intent.getAction()==null){
                if(mReceiver==null){
                    mReceiver = new Screen_Recevier();
                    IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                    filter.addAction(Intent.ACTION_SCREEN_ON);
                    registerReceiver(mReceiver,filter);
                }
            }
        }
        return START_REDELIVER_INTENT;      //이후 서비스 재생성 가능, 강제로 종료되기 전에 전달된 마지막 Intent를 다시 전달해주는 기능 포함.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("111", "onDestroy: 서비스스크린");
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
        }
    }
}