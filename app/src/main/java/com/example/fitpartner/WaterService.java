package com.example.fitpartner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class WaterService extends Service {
    public WaterService() {
    }


    private String Filename;
    private NotificationCompat.Builder serviceBuilder;
    String receiveWater;
    NotificationManager manager;


    @Override
    public void onCreate() {
        Log.d("111", "onCreate: ");
        super.onCreate();
        //오레오에서는 이 채널을 추가해줘야함
        //이 코드는 한번만 실행해주면되니까 액티비티에서 수행을 해도되고 서비스의 생성자에서 수행을 해도된다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default","기본 채널",NotificationManager.IMPORTANCE_LOW));
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("111", "onStartCommand: 받음");
        receiveWater = intent.getStringExtra("change");
        String btn_push = intent.getStringExtra("btn_push");
        if ("startForeground".equals(intent.getAction())){
            Filename = intent.getStringExtra("date");
            Log.d("111", "onStartCommand: ");
            serviceBuilder = startForegroundService();
        } else if ("stopForeground".equals(intent.getAction())){
            Log.d("111", "onStartCommand: stop포그라운드");
            Intent stopintent = new Intent(getApplicationContext(), Screen_Service.class);
            stopService(stopintent);
            stopForeground(true);
        } else {
            Log.d("111", "onStartCommand: 스타트도 스탑도아님");
            if (receiveWater != null){
                Log.d("111", "onStartCommand:바꿔주는곳인데 " + Filename);
                SharedPreferences sharedPreferences = getSharedPreferences(Filename, Context.MODE_PRIVATE);
                String waters = sharedPreferences.getString("Water","0");
                serviceBuilder.setContentText("오늘 섭취한 수분량 : " + waters + "mL");
                manager.notify(1,serviceBuilder.build());
            } else {
                Log.d("111", "onStartCommand: 잘못들어가고있음");
            }
            if (btn_push != null){
                Log.d("111", "onStartCommand: 액션버튼눌림");
            }
        }


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private NotificationCompat.Builder startForegroundService(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"default");
        //오레오 이상에서는 채널이 필요하다 뒤에있는게 채널 이름
        Intent intent = new Intent(getApplicationContext(), Screen_Service.class);
        startService(intent);
        builder.setSmallIcon(R.drawable.app_icon);
        builder.setContentTitle("WaterPartner");
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.water));
        SharedPreferences sharedPreferences = getSharedPreferences(Filename, Context.MODE_PRIVATE);
        String waters = sharedPreferences.getString("Water","0");
        builder.setContentText("오늘 섭취한 수분량 : " + waters + "mL");

        //실제 동작하는 인텐트
        Intent notificationIntent = new Intent(this,MainActivity.class);
        //클릭했을떄 동작하는 인텐트 (인텐트를 잠시 대기시켜놓는다)
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        //클릭했을떄 일어나는 실행되는 팬딩인텐트 설정
        builder.setContentIntent(pendingIntent);

        /*Intent updateIntent = new Intent(startForeground(1,builder.build()));
        updateIntent.putExtra("btn_push","1");
        PendingIntent pendingUpdateIntent = PendingIntent.getActivity(this,0,updateIntent,0);

        //action 추가용 버튼들
        builder.addAction(R.drawable.ic_baseline_remove_circle_outline_24,"뺴기",pendingUpdateIntent);
        builder.addAction(R.drawable.ic_baseline_add_circle_outline_24,"더하기",pendingIntent);*/


        //이제 notification실행해야함
        startForeground(1, builder.build());

        return builder;
    }


}