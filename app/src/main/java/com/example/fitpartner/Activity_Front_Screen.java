package com.example.fitpartner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Activity_Front_Screen extends AppCompatActivity {

    private final String mainData = "MainData";


    long now = System.currentTimeMillis();
    Date mDate = new Date(now);
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
    String dateData = dateformat.format(mDate);

    private final String Date = dateData;
    private int water_calculate;
    private int water_target;

    CircleProgressBar circleProgressBar;

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("111", "onResume: ");
        //현재 수분량 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences(Date, Context.MODE_PRIVATE);
        String water = sharedPreferences.getString("Water","0");
        water_calculate = Integer.parseInt(water);


        //목표 수분량 가져오기
        SharedPreferences sharedPreferencesTarget = getSharedPreferences(mainData, Context.MODE_PRIVATE);
        String target = sharedPreferencesTarget.getString("targetWater","0");
        water_target = Integer.parseInt(target);


        //진행률 설정
        circleProgressBar.setProgress((water_calculate*100)/water_target);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_screen);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        Log.d("111", "onCreate: 스크린표시액티비티");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Button btn_increase = (Button) findViewById(R.id.button_increase);
        Button btn_decrease = (Button) findViewById(R.id.button_decrease);
        TextView tv_screenWater = (TextView) findViewById(R.id.textView_screen_water);
        circleProgressBar = (CircleProgressBar) findViewById(R.id.cpb_circlebar);

        //현재 수분량 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences(Date, Context.MODE_PRIVATE);
        String water = sharedPreferences.getString("Water","0");
        water_calculate = Integer.parseInt(water);


        //목표 수분량 가져오기
        SharedPreferences sharedPreferencesTarget = getSharedPreferences(mainData, Context.MODE_PRIVATE);
        String target = sharedPreferencesTarget.getString("targetWater","0");
        water_target = Integer.parseInt(target);


        //진행률 설정
        circleProgressBar.setProgress((water_calculate*100)/water_target);



        tv_screenWater.setText("오늘 섭취한 수분\n" + water + "mL");

        btn_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("111", "onClick: 잠금화면 +");
                water_calculate += 50;
                SharedPreferences sharedPreferences = getSharedPreferences(Date, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Water",water_calculate+"");
                editor.apply();

                //프로그래스바
                circleProgressBar.setProgress((water_calculate*100)/water_target);

                //notification 업데이트용
                Intent intent = new Intent(getApplicationContext(), WaterService.class);
                intent.putExtra("change","1");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                    Log.d("111", "onClick: ");
                }

                if (water_calculate >= water_target){
                    tv_screenWater.setText("목표한 수분량을\n모두 채웠어요!");
                } else {
                    tv_screenWater.setText("오늘 섭취한 수분\n" + water_calculate + "mL");
                }


            }
        });

        btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("111", "onClick: 잠금화면 -");
                if (water_calculate == 0){
                    water_calculate = 0;
                } else {
                    water_calculate -= 50;
                }
                tv_screenWater.setText("오늘 섭취한 수분\n" + water_calculate + "mL");
                SharedPreferences sharedPreferences = getSharedPreferences(Date, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Water",water_calculate+"");
                editor.apply();

                //프로그래스바용
                circleProgressBar.setProgress((water_calculate*100)/water_target);

                Intent intent = new Intent(getApplicationContext(), WaterService.class);
                intent.putExtra("change","1");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                    Log.d("111", "onClick: ");
                }
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this,null);
        } else {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }

    }
}