package com.example.fitpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

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
    //private ImageButton imgbtn_mypage; //마이페이지 버튼
    private ImageButton imgbtn_stopWatch; //스톱워치 버튼
    private ImageButton imgbtn_chart; //차트보기 버튼
    private AdView mAdView;

    static String today;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==PackageManager.PERMISSION_GRANTED){
                Log.d("permission", "권한 설정 완료");
            }
            else {
                Log.d("permission", "권한 설정 요청");

                ActivityCompat.requestPermissions(MainActivity.this,new String[]
                        {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }


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


        //스톱워치
        imgbtn_stopWatch = findViewById(R.id.imageButton_stopWatch);
        imgbtn_stopWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Activity_StopWatch.class);
                startActivity(intent);
            }
        });

        //차트화면으로 이동
        imgbtn_chart = findViewById(R.id.imageButton_chart);
        imgbtn_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Activity_Chart.class);
                startActivity(intent);
            }
        });

        //하단 네비게이션바 프래그먼트
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
    //권한 요청 결과 받는곳
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        Log.d("permission", "onRequestPermissionsResult: ");
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            Log.d("permission", "PERMISSION: " + permissions[0] + "was" + grantResults[0]);
        }else {
            Toast.makeText(this, "권한을 허용해주세요\n기능 사용이 제한될 수 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}