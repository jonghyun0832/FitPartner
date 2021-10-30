package com.example.fitpartner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class WaterService extends Service {
    public WaterService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ("startForeground".equals(intent.getAction())){
            Log.d("111", "onStartCommand: ");
            startForegroundService();
        } else if ("stopForeground".equals(intent.getAction())){
            Log.d("111", "onStartCommand: stop포그라운드");
            stopForeground(true);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startForegroundService(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"default");
        //오레오 이상에서는 채널이 필요하다 뒤에있는게 채널 이름
        builder.setSmallIcon(R.drawable.app_icon);
        builder.setContentTitle("WaterPartner");
        builder.setContentText("수분 섭취량을 기록하세요.");

        //실제 동작하는 인텐트
        Intent notificationIntent = new Intent(this,MainActivity.class);
        //클릭했을떄 동작하는 인텐트 (인텐트를 잠시 대기시켜놓는다)
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        //클릭했을떄 일어나는 실행되는 팬딩인텐트 설정
        builder.setContentIntent(pendingIntent);

        //action 추가용 버튼들
        builder.addAction(android.R.drawable.btn_plus,"plus",pendingIntent);
        builder.addAction(android.R.drawable.btn_minus,"minus",pendingIntent);



        //오레오에서는 이 채널을 추가해줘야함
        //이 코드는 한번만 실행해주면되니까 액티비티에서 수행을 해도되고 서비스의 생성자에서 수행을 해도된다.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default","기본 채널",NotificationManager.IMPORTANCE_DEFAULT));
        }


        //이제 notification실행해야함
        startForeground(1, builder.build());


    }


}