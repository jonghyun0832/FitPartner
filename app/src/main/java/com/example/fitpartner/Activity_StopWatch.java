package com.example.fitpartner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class Activity_StopWatch extends AppCompatActivity {

    private int time_msec = 0;
    private int time_sec = 0;
    private int time_min = 0;
    private int i;
    private int cnt_click;
    Button btn_leftTimer;
    Button btn_rightTimer;
    TextView tv_timer;
    Handler handler;
    Thread thread;
    boolean isRunning;
    TimeRunnable timeRunnable;


    private ArrayList<StopWatchData> timeArray;
    private StopWatchAdapter timeAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_watch);

        btn_leftTimer = findViewById(R.id.button_leftTimer);
        btn_rightTimer = findViewById(R.id.button_rightTimer);
        tv_timer = findViewById(R.id.textView_timer);

        //클릭 0으로 초기화
        cnt_click = 0;

        //텍스트 초기화
        Timerset();

        //리사이클러뷰
        recyclerView = (RecyclerView)findViewById(R.id.rv_record);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        timeArray = new ArrayList<>();

        timeAdapter = new StopWatchAdapter(timeArray);
        recyclerView.setAdapter(timeAdapter);

        btn_rightTimer.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.round_button3));


        btn_leftTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cnt_click += 1;
                if (cnt_click == 0){
                    //시작 / 구간기록(클릭안됨) 이건 오른쪽에서 구현


                } else if (cnt_click == 1){
                    btn_leftTimer.setText("중단");
                    btn_leftTimer.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.round_button2));
                    btn_rightTimer.setText("구간기록");
                    btn_rightTimer.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.round_button));

                    //스레드 시작
                    isRunning = true;
                    handler = new Handler();
                    timeRunnable = new TimeRunnable();
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(isRunning){
                                try{
                                    Thread.sleep(10);
                                    handler.post(timeRunnable);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    break;
                                }
                            }
                        }
                    });
                    thread.start();

                } else {
                    btn_leftTimer.setText("계속");
                    btn_leftTimer.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.round_button));
                    btn_rightTimer.setText("초기화");
                    cnt_click = 0; //눌렀을떄 다시 스레드돌게

                    //스레드 일시중지
                    thread.interrupt();
                    isRunning = false;


                }
            }
        });

        btn_rightTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cnt_click == 0){
                    //초기화 시키는 부분임
                    btn_leftTimer.setText("시작");
                    btn_rightTimer.setText("구간기록");
                    btn_rightTimer.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.round_button3));

                    i = 0;
                    time_min = 0;
                    time_sec = 0;
                    time_msec = 0;

                    Timerset();

                    timeArray.clear();
                    timeAdapter.notifyDataSetChanged();



                    //시간도 초기화 시켜줘야함

                } else if (cnt_click == 1){
                    //구간기록기능 구현
                    //리사이클러뷰 추가하는곳
                    String minute = Integer.toString(time_min);
                    String second = Integer.toString(time_sec);
                    String milisecond = Integer.toString(time_msec);

                    if (time_min < 10) {
                        minute = "0" + minute;
                    }
                    if (time_sec < 10) {
                        second = "0" + second;
                    }
                    if (time_msec < 10){
                        milisecond = "0" + milisecond;
                    }

                    String time = (minute + " : " + second + " . " + milisecond);
                    StopWatchData record = new StopWatchData(time);

                    timeArray.add(record);
                    timeAdapter.notifyDataSetChanged();

                    recyclerView.scrollToPosition(timeArray.size()-1);
                }
            }
        });

    }

    private void Timerset() {
        String minute = Integer.toString(time_min);
        String second = Integer.toString(time_sec);
        String milisecond = Integer.toString(time_msec);

        if (time_min < 10) {
            minute = "0" + minute;
        }
        if (time_sec < 10) {
            second = "0" + second;
        }
        if (time_msec < 10){
            milisecond = "0" + milisecond;
        }

        tv_timer.setText(minute + " : " + second + " . " + milisecond);
    }


    public class TimeRunnable implements Runnable {
        @Override
        public void run() {
            Log.d("111", "run: " + i);
            i += 1;
            time_msec = i%100;
            time_sec = (i/100)%60;
            time_min = (i/(100*60))%60;
            Timerset();

        }
    }

}