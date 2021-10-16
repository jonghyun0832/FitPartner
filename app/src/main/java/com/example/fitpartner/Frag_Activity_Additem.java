package com.example.fitpartner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Frag_Activity_Additem extends AppCompatActivity {

    private ImageView iv_titleBtn;
    private ImageView iv_dataBtn;
    private EditText et_exTitle;
    private EditText et_exData;
    private TextView tv_startTime;
    private TextView tv_endTime;
    private Button btn_add;
    private Button btn_back;

    private int starthour;
    private int startminute;
    private int endhour;
    private int endminute;

    //현재시간가져오기
    long now =System.currentTimeMillis();
    Date mDate = new Date(now);
    SimpleDateFormat hourData = new SimpleDateFormat("HH");
    SimpleDateFormat minuteData = new SimpleDateFormat("mm");
    //시
    String getHour = hourData.format(mDate);
    //분
    String getMinute = minuteData.format(mDate);
    //시(int)
    int transHour = Integer.parseInt(getHour) + 1;
    //타임피커 설정용
    int pickerHour = Integer.parseInt(getHour);
    int pickerMinute = Integer.parseInt(getMinute);
    int pickerMinute2 = pickerMinute;




    //타임피커 리스너
    private TimePickerDialog.OnTimeSetListener startlistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            starthour = hour;
            startminute = minute;
            pickerHour = starthour;
            pickerMinute = startminute;
            if (minute < 10){
                tv_startTime.setText(hour + " : 0" +minute);
            } else{
                tv_startTime.setText(hour + " : " + minute);
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener endlistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            tv_endTime.setText(hour + " : " + minute);
            endhour = hour;
            endminute = minute;
            transHour = endhour;
            pickerMinute2 = endminute;
            if (minute < 10){
                tv_endTime.setText(hour + " : 0" +minute);
            } else {
                tv_endTime.setText(hour + " : " + minute);
            }
        }
    };


    private static final String TAG = "AddItem";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frag_additem);
        Log.d(TAG, "onCreate: ");


        //레이아웃 연결
        iv_titleBtn = (ImageView)findViewById(R.id.iv_TitleBtn);
        iv_dataBtn = (ImageView)findViewById(R.id.iv_DataBtn);
        et_exTitle = (EditText)findViewById(R.id.editText_exTitle);
        et_exData = (EditText)findViewById(R.id.editText_exData);
        tv_startTime = (TextView)findViewById(R.id.textView_startTime);
        tv_endTime = (TextView)findViewById(R.id.textView_endTime);
        btn_add = (Button)findViewById(R.id.button_additem);
        btn_back = (Button)findViewById(R.id.button_back);

        //직접 텍스트 수정불가
        et_exTitle.setFocusable(false);
        et_exData.setFocusable(false);

        //처음 열렸을때 타임피커 텍스트 기본시간설정
        tv_startTime.setText(getHour + " : " + getMinute);
        tv_endTime.setText(transHour + " : " + getMinute);


        //운동 제목 기록
        iv_titleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick1: ");
                addMessage(et_exTitle);

            }
        });

        //운동 내용 기록
        iv_dataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick2: ");
                addMessage(et_exData);

            }
        });

        //시작시간 눌렀을때
        tv_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimePicker();
            }
        });

        //종료시간 눌렀을때

        tv_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTimePicker();
            }
        });


        //추가하기 버튼 눌렀을때
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(starthour > endhour) {
                    Toast.makeText(getApplicationContext(), "시작시간이 종료시간보다 뒤에 있습니다.", Toast.LENGTH_SHORT).show();
                } else if (starthour == endhour){
                    if(startminute > endminute){
                        Toast.makeText(getApplicationContext(), "시작시간이 종료시간보다 뒤에 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else{ //정상적인 시작시간과 종료시간일 경우
                    Log.d(TAG, "onClick: Intent송신 ");
                }

            }
        });

        //돌아가기 눌렀을때
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });


    }

    public void addMessage(EditText editText){
        Log.d(TAG, "addMessage: ");
        AlertDialog.Builder ad = new AlertDialog.Builder(Frag_Activity_Additem.this);
        ad.setIcon(R.mipmap.ic_launcher);
        ad.setTitle("정보 입력");
        ad.setMessage("정보입력메세지입니다.");

        final EditText et = new EditText(Frag_Activity_Additem.this);
        ad.setView(et);

        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String result = et.getText().toString();
                editText.setText(result);
                dialogInterface.dismiss();
            }
        });
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = ad.create();
        alertDialog.show();
    }

    public void startTimePicker(){
        TimePickerDialog dialog = new TimePickerDialog(Frag_Activity_Additem.this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,startlistener,pickerHour,pickerMinute,true);
        dialog.setTitle("시작시간");
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    public void endTimePicker(){
        TimePickerDialog dialog = new TimePickerDialog(Frag_Activity_Additem.this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,endlistener,transHour,pickerMinute2,true);
        dialog.setTitle("종료시간");
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

}