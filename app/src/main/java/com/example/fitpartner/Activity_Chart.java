package com.example.fitpartner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Activity_Chart extends AppCompatActivity {

    private LineChart chart_weight;
    private LineChart chart_fat;
    private LineChart chart_protein;

    private final String mainData = "MainData";

    ArrayList<BodyData> bodyArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        //차트 등록
        chart_weight = findViewById(R.id.linechart_weight);
        chart_fat = findViewById(R.id.linechart_fat);
        chart_protein = findViewById(R.id.linechart_protein);

        //차트 설정
        chart_weight.setBackgroundColor(ContextCompat.getColor(this,R.color.background));
        chart_weight.setDrawGridBackground(false);
        chart_weight.setDescription(null);

        //차트 데이터 설정
        ArrayList<Entry> entries_weight = new ArrayList<>();
        ArrayList<Entry> entries_fat = new ArrayList<>();
        ArrayList<Entry> entries_protein = new ArrayList<>();
        ArrayList<String> xlabel_title = new ArrayList<>();

        //데이터 받아오기
        bodyArrayList = new ArrayList<>();

        SharedPreferences sharedPreferences = this.getSharedPreferences(mainData, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Bodylog")) {
            Gson gson = new GsonBuilder().create();
            String json = sharedPreferences.getString("Bodylog", "");
            Type bodylogType = new TypeToken<ArrayList<BodyData>>() {
            }.getType();
            bodyArrayList = gson.fromJson(json, bodylogType);
        }

        //순서 뒤집에서 넣어주기
        String compareDate = "";
        ArrayList avgList = new ArrayList();
        for (int i = bodyArrayList.size() - 1; i >= 0; i--){
            String date = bodyArrayList.get(i).getCreateDate();
            if (compareDate.equals(date)){
                //평균내기
            } else{
                String [] tmparray = bodyArrayList.get(i).getTv_totalWeight().split(" ");
                int weight = Integer.parseInt(tmparray[2]);//몸무게 가져오기
                tmparray = bodyArrayList.get(i).getTv_fatRate().split(" "); //ex) 체지방량 : 24kg
                int fat = Integer.parseInt(tmparray[2]); //지방량 가져오기
                tmparray = bodyArrayList.get(i).getTv_proteinRate().split(" ");
                int protein = Integer.parseInt(tmparray[2]); //근육량 가져오기

                avgList.add(i);
                avgList.add(weight);
                avgList.add(fat);
                avgList.add(protein);
                // i값으로 엔트리 등록해서 빵꾸가 계속 뚫리고있는데 이거 해결할 방법이 없을까 좀 고민해보자
                // 이거 전값인데 가져와서 어떻게 잘 해주면 되지 않을까 한번 잘 생각해보자

                entries_weight.add(new Entry(bodyArrayList.size()-i-1,weight));
                entries_fat.add(new Entry(bodyArrayList.size()-i-1,fat));
                entries_protein.add(new Entry(bodyArrayList.size()-i-1,protein));
                xlabel_title.add(" ");
            }
            compareDate = date;

        }


        //차트에 들어갈 데이터 설정
        /*ArrayList<Entry> entries_weight = new ArrayList<>();
        entries_weight.add(new Entry(0,80));
        entries_weight.add(new Entry(1,81.8f));
        entries_weight.add(new Entry(2,83));
        entries_weight.add(new Entry(3,79.5f));*/
        //몸무게 엔트리
        LineDataSet lineDataSet_weight = new LineDataSet(entries_weight, "몸무게");
        lineDataSet_weight.setLineWidth(2); // 선 굵기
        lineDataSet_weight.setCircleRadius(6); // 곡률

        lineDataSet_weight.setCircleColor(ContextCompat.getColor(this, R.color.bar_text)); // LineChart에서 Line Circle Color 설정
        lineDataSet_weight.setCircleHoleColor(ContextCompat.getColor(this, R.color.bar_text)); // LineChart에서 Line Hole Circle Color 설정
        lineDataSet_weight.setColor(ContextCompat.getColor(this, R.color.bar_text)); // LineChart에서 Line Color 설정


        /*ArrayList<Entry> entries_fat = new ArrayList<>();
        entries_fat.add(new Entry(0,20));
        entries_fat.add(new Entry(1,21.8f));
        entries_fat.add(new Entry(2,23));
        entries_fat.add(new Entry(3,16.5f));*/
        //체지방 엔트리
        LineDataSet lineDataSet_fat = new LineDataSet(entries_fat, "체지방량");
        lineDataSet_fat.setLineWidth(2); // 선 굵기
        lineDataSet_fat.setCircleRadius(6); // 곡률

        lineDataSet_fat.setCircleColor(ContextCompat.getColor(this, R.color.graphColor)); // LineChart에서 Line Circle Color 설정
        lineDataSet_fat.setCircleHoleColor(ContextCompat.getColor(this, R.color.graphColor)); // LineChart에서 Line Hole Circle Color 설정
        lineDataSet_fat.setColor(ContextCompat.getColor(this, R.color.graphColor)); // LineChart에서 Line Color 설정

        //단백질 엔트리
        LineDataSet lineDataSet_protein = new LineDataSet(entries_protein, "근골격량");
        lineDataSet_protein.setLineWidth(2); // 선 굵기
        lineDataSet_protein.setCircleRadius(6); // 곡률

        lineDataSet_protein.setCircleColor(ContextCompat.getColor(this, R.color.purple_500)); // LineChart에서 Line Circle Color 설정
        lineDataSet_protein.setCircleHoleColor(ContextCompat.getColor(this, R.color.purple_500)); // LineChart에서 Line Hole Circle Color 설정
        lineDataSet_protein.setColor(ContextCompat.getColor(this, R.color.purple_500)); // LineChart에서 Line Color 설정


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet_weight);
        dataSets.add(lineDataSet_fat);
        dataSets.add(lineDataSet_protein);

        //x축 설정
        XAxis xAxis = chart_weight.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //아래쪽으로
        xAxis.setTextColor(R.color.black); //x축 텍스트 컬러
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xlabel_title));

        // y축 설정
        YAxis yLAxis = chart_weight.getAxisLeft();
        yLAxis.setTextColor(R.color.black); // y축 텍스트 컬러

        // y축 오른쪽 비활성화
        YAxis yRAxis = chart_weight.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);


        LineData lineData = new LineData(dataSets);
        chart_weight.setData(lineData);
        //lineData.setValueTextColor(ContextCompat.getColor(getContext(), R.color.black));
        //lineData.setValueTextSize(9);

        /*// x축 설정
        view.setLineData(lineData);
        xAxis = view.getXAxis(); // x축에 대한 정보를 View로부터 받아온다.
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // x축 표시에 대한 위치 설정으로 아래쪽에 위치시킨다.
        xAxis.setTextColor(R.color.black); // x축 텍스트 컬러 설정*/



    }
}