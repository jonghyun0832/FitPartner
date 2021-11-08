package com.example.fitpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView; //바텀 네이게이션 뷰 (하단바)
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Frag_Progress frag_progress; // 달성도
    private Frag_FoodLog frag_foodLog;  //식습관 관리
    private Frag_WorkoutLog frag_workoutLog; //운동 일지
    private Frag_PictureLog frag_pictureLog; //사진 일지
    private ImageButton imgbtn_mypage; //마이페이지 버튼
    private ImageButton imgbtn_stopWatch; //스톱워치 버튼
    private ImageButton imgbtn_chart; //차트보기 버튼
    private AdView mAdView;

    static String today;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("111", "onCreate: 이건한번만");
        setContentView(R.layout.activity_main);


        //광고베너
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        mAdView = findViewById(R.id.adView_main);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
        String dateData = dateformat.format(mDate);
        today = dateData;

        //마이페이지
        imgbtn_mypage = findViewById(R.id.imageButton_mypage);
        imgbtn_mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SubActivity_Mypage.class);
                startActivity(intent);
            }
        });

        //스톱워치
        imgbtn_stopWatch = findViewById(R.id.imageButton_stopWatch);
        imgbtn_stopWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Activity_StopWatch.class);
                startActivity(intent);
            }
        });

        imgbtn_chart = findViewById(R.id.imageButton_chart);
        imgbtn_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Activity_Chart.class);
                startActivity(intent);
            }
        });


        //오늘날짜 전역변수로 세팅
        //((StaticItem)getApplication()).setDate(dateData);

        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_progress:
                        setFrag(0);
                        break;
                    case R.id.action_workoutLog:
                        setFrag(1);
                        break;
                    case R.id.action_pictureLog:
                        setFrag(2);
                        break;
                    case R.id.action_foodLog:
                        setFrag(3);
                        break;
                }
                return true;
            }
        });

        frag_progress = new Frag_Progress();
        frag_foodLog = new Frag_FoodLog();
        frag_workoutLog = new Frag_WorkoutLog();
        frag_pictureLog = new Frag_PictureLog();
        setFrag(0); //첫 프래그먼트 화면을 무엇으로 지정할것인지 선택

    }

    //프래그먼트 교체가 일어나는 실행문이다.
    public void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n){
            case 0:
                ft.replace(R.id.main_frame,frag_progress);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame,frag_workoutLog);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame,frag_pictureLog);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame,frag_foodLog);
                ft.commit();
                break;
        }
    }




}