package com.example.fitpartner;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Activity_Chart extends AppCompatActivity {

    private LineChart chart_body;
    private Button btn_popup;
    private TextView tv_fitSteps, tv_fitCalories;
    private ImageButton imgbtn_setTargetStep;

    long now = System.currentTimeMillis();
    Date mDate = new Date(now);
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
    String dateData = dateformat.format(mDate);

    private final String FileName = dateData;
    private final String mainData = "MainData";
    private final String FitData = "FitData";

    ArrayList<BodyData> bodyArrayList;

    private int dataSize = 7; //?????? ?????? ????????? ??????
    private int targetSteps; //?????? ?????????
    int stepTotal; //???????????? ????????? ?????? ???
    double rounfoffCalories = 0.0 ; //?????????

    private boolean isActivate = true; //????????? ????????????

    CircleProgressBar circleProgressBar; //?????? ?????????

    private static final int REQUEST_OAUTH = 1; //OAUTH

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        //?????? ??????
        chart_body = (LineChart)findViewById(R.id.linechart_body);
        btn_popup = (Button)findViewById(R.id.button_popupbody);
        tv_fitSteps = (TextView)findViewById(R.id.textView_fitSteps);
        tv_fitCalories = (TextView)findViewById(R.id.textView_fitCalories);
        circleProgressBar = (CircleProgressBar) findViewById(R.id.cpb_circlebar_step);
        imgbtn_setTargetStep = (ImageButton)findViewById(R.id.imageButton_setTargetStep);


        Intent intent = Activity_Chart.this.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.fitness");
        //?????? ????????? ?????????
        if (intent == null){
            Toast.makeText(this, "daily fitness??? ??????????????????\n??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
            //???????????? ?????? ????????????
            tv_fitSteps.setText("Google Fit??? \n??????????????????");
            tv_fitCalories.setText("");
            //?????? ????????? ????????? ????????????, ?????????????????????
            imgbtn_setTargetStep.setVisibility(View.INVISIBLE);
            imgbtn_setTargetStep.setEnabled(false);


        }
        //?????? ????????? ?????????
        else {

            // ?????? ?????? ????????? ????????? ?????? ??????
            GoogleSignInOptionsExtension fitnessOptions =
                    FitnessOptions.builder()
                            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                            .build();
            // ?????? ?????? ???????????? ?????????
            GoogleSignInAccount googleSignInAccount =
                    GoogleSignIn.getAccountForExtension(this, fitnessOptions);
            //?????? ????????????
            if (!GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions)) {
                GoogleSignIn.requestPermissions(
                        this, // your activity
                        REQUEST_OAUTH, // e.g. 1
                        googleSignInAccount,
                        fitnessOptions);
            }
            //??????????????? ????????? / ????????? ????????????
            else {
                if (isActivate == true){
                    readDataSteps(); //????????? ????????????
                    readDataCalories(); //?????????????????????
                    isActivate = false;
                }
            }
        }

        imgbtn_setTargetStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(Activity_Chart.this);
                ad.setTitle("?????? ????????? ????????????");

                EditText et_targetSteps = new EditText(Activity_Chart.this);
                et_targetSteps.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                //????????? ????????? ????????????
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(mainData, Context.MODE_PRIVATE);
                String targetStep = sharedPreferences.getString("targetStep","0");

                if (targetStep != null){
                    //???????????? ????????? ???????????????
                    et_targetSteps.setText(targetStep);
                } else {
                    //???????????? ????????? ????????????
                    et_targetSteps.setHint("???????????? ??????????????????.");
                }

                ad.setView(et_targetSteps);

                ad.setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(mainData, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String targetStep = et_targetSteps.getText().toString();
                        editor.putString("targetStep",targetStep);
                        editor.apply();
                        dialogInterface.dismiss();
                        readDataSteps(); //????????? ????????????
                        readDataCalories(); //?????????????????????
                    }
                });
                ad.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = ad.create();
                alertDialog.show();

            }
        });


        //?????? ?????????
        btn_popup.setText("?????? 7???");
        btn_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),view);
                getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.action_menu1){
                            dataSize = 4;

                        } else if (menuItem.getItemId() == R.id.action_menu2){
                            dataSize = 7;

                        } else {
                            dataSize = 14;
                        }
                        setChart_body(dataSize);
                        chart_body.animateX(1000);
                        chart_body.invalidate();
                        btn_popup.setText("?????? " + dataSize + "???");
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


        //?????? ??????
        chart_body.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
        chart_body.setDrawGridBackground(false);
        chart_body.setDescription(null);
        chart_body.setExtraOffsets(30,10,30,10);

        //???????????????
        setChart_body(dataSize);

    }

    private void setChart_body(int dataSize){

        ArrayList<Entry> entries_weight = new ArrayList<>();
        ArrayList<Entry> entries_fat = new ArrayList<>();
        ArrayList<Entry> entries_protein = new ArrayList<>();
        ArrayList<String> xlabel_title = new ArrayList<>();

        Log.d("111", "setChart_body: ");

        //????????? ???????????? - ???????????????
        bodyArrayList = new ArrayList<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences(mainData, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Bodylog")) {
            Gson gson = new GsonBuilder().create();
            String json = sharedPreferences.getString("Bodylog", "");
            Type bodylogType = new TypeToken<ArrayList<BodyData>>() {
            }.getType();
            bodyArrayList = gson.fromJson(json, bodylogType);
        }

        //??????????????? ????????? ????????? ?????????
        ArrayList<ArrayList<String>> arrangeList = new ArrayList<>(); //????????? ????????? ????????? ?????????
        for (int i = 0; i < bodyArrayList.size(); i++){
            String date = bodyArrayList.get(i).getCreateDate();
            String [] tmparray = bodyArrayList.get(i).getTv_totalWeight().split(" ");
            String weight = tmparray[2];//????????? ????????????
            tmparray = bodyArrayList.get(i).getTv_fatRate().split(" "); //ex) ???????????? : 24kg
            String fat = tmparray[2]; //????????? ????????????
            tmparray = bodyArrayList.get(i).getTv_proteinRate().split(" ");
            String protein = tmparray[2]; //????????? ????????????
            ArrayList<String> tmpList  = new ArrayList<String>();

            tmpList.add(date);
            tmpList.add(weight);
            tmpList.add(fat);
            tmpList.add(protein);

            arrangeList.add(tmpList);
        }

        //????????? ???????????? ???????????? ??????????????? ???????????? ????????????
        ArrayList<ArrayList<String>> cleanarr = new ArrayList<>();
        ArrayList<ArrayList<String>> tmparr = new ArrayList<>();
        for (int i = 0; i < arrangeList.size(); i++){
            if (i != arrangeList.size()-1){
                if (arrangeList.get(i).get(0).equals(arrangeList.get(i+1).get(0))){
                    //??????????????? ?????? ??????????????? ????????????
                    tmparr.add(arrangeList.get(i));
                }
                else {
                    tmparr.add(arrangeList.get(i));
                    int tmpWeight = 0;
                    int tmpFat = 0;
                    int tmpProtein = 0;
                    for (int j = 0; j < tmparr.size(); j++){
                        tmpWeight += Integer.parseInt(tmparr.get(j).get(1));
                        tmpFat += Integer.parseInt(tmparr.get(j).get(2));
                        tmpProtein += Integer.parseInt(tmparr.get(j).get(3));
                    }
                    String totalWeight = Integer.toString(tmpWeight/tmparr.size());
                    String totalFat = Integer.toString(tmpFat/tmparr.size());
                    String totalProtein = Integer.toString(tmpProtein/tmparr.size());
                    tmparr.get(0).set(1,totalWeight);
                    tmparr.get(0).set(2,totalFat);
                    tmparr.get(0).set(3,totalProtein);
                    cleanarr.add(tmparr.get(0));
                    tmparr.clear();
                }
            }
            else {
                //i??? ????????? ?????????
                tmparr.add(arrangeList.get(i));
                int tmpWeight = 0;
                int tmpFat = 0;
                int tmpProtein = 0;
                for (int j = 0; j < tmparr.size(); j++){
                    tmpWeight += Integer.parseInt(tmparr.get(j).get(1));
                    tmpFat += Integer.parseInt(tmparr.get(j).get(2));
                    tmpProtein += Integer.parseInt(tmparr.get(j).get(3));
                }
                String totalWeight = Integer.toString(tmpWeight/tmparr.size());
                String totalFat = Integer.toString(tmpFat/tmparr.size());
                String totalProtein = Integer.toString(tmpProtein/tmparr.size());
                tmparr.get(0).set(1,totalWeight);
                tmparr.get(0).set(2,totalFat);
                tmparr.get(0).set(3,totalProtein);
                cleanarr.add(tmparr.get(0));
                tmparr.clear();
            }
        }

        List<ArrayList<String>> cleanarr_cut;

        if (dataSize < cleanarr.size()){
            cleanarr_cut = cleanarr.subList(cleanarr.size()-dataSize,cleanarr.size());
        } else {
            cleanarr_cut = cleanarr;
        }

        //?????? ???????????? ????????????
        for (int i = cleanarr_cut.size() - 1; i >= 0; i--){
            String date = cleanarr.get(i).get(0);
            int weight = Integer.parseInt(cleanarr.get(i).get(1));//????????? ????????????
            int fat = Integer.parseInt(cleanarr.get(i).get(2)); //????????? ????????????
            int protein = Integer.parseInt(cleanarr.get(i).get(3)); //????????? ????????????

            //????????? ????????? ????????? ??????
            entries_weight.add(new Entry(cleanarr_cut.size()-i-1,weight));
            entries_fat.add(new Entry(cleanarr_cut.size()-i-1,fat));
            entries_protein.add(new Entry(cleanarr_cut.size()-i-1,protein));
            //?????????, ??????????????? ?????? ??????
            if (i == cleanarr_cut.size()-1 || i == 0){
                xlabel_title.add(date);
            } else {
                xlabel_title.add(" ");
            }

        }

        //????????? ?????????
        LineDataSet lineDataSet_weight = new LineDataSet(entries_weight, "?????????");
        lineDataSet_weight.setLineWidth(2); // ??? ??????
        lineDataSet_weight.setCircleRadius(6); // ??????

        lineDataSet_weight.setCircleColor(ContextCompat.getColor(this, R.color.teal_200)); // LineChart?????? Line Circle Color ??????
        lineDataSet_weight.setCircleHoleColor(ContextCompat.getColor(this, R.color.teal_200)); // LineChart?????? Line Hole Circle Color ??????
        lineDataSet_weight.setColor(ContextCompat.getColor(this, R.color.teal_200)); // LineChart?????? Line Color ??????


        //????????? ?????????
        LineDataSet lineDataSet_fat = new LineDataSet(entries_fat, "????????????");
        lineDataSet_fat.setLineWidth(2); // ??? ??????
        lineDataSet_fat.setCircleRadius(6); // ??????

        lineDataSet_fat.setCircleColor(ContextCompat.getColor(this, R.color.graphColor)); // LineChart?????? Line Circle Color ??????
        lineDataSet_fat.setCircleHoleColor(ContextCompat.getColor(this, R.color.graphColor)); // LineChart?????? Line Hole Circle Color ??????
        lineDataSet_fat.setColor(ContextCompat.getColor(this, R.color.graphColor)); // LineChart?????? Line Color ??????

        //????????? ?????????
        LineDataSet lineDataSet_protein = new LineDataSet(entries_protein, "????????????");
        lineDataSet_protein.setLineWidth(2); // ??? ??????
        lineDataSet_protein.setCircleRadius(6); // ??????

        lineDataSet_protein.setCircleColor(ContextCompat.getColor(this, R.color.purple_500)); // LineChart?????? Line Circle Color ??????
        lineDataSet_protein.setCircleHoleColor(ContextCompat.getColor(this, R.color.purple_500)); // LineChart?????? Line Hole Circle Color ??????
        lineDataSet_protein.setColor(ContextCompat.getColor(this, R.color.purple_500)); // LineChart?????? Line Color ??????


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet_weight);
        dataSets.add(lineDataSet_fat);
        dataSets.add(lineDataSet_protein);

        //x??? ??????
        XAxis xAxis = chart_body.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //???????????????
        xAxis.setTextColor(Color.BLACK); //x??? ????????? ??????
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xlabel_title));
        //xAxis.setTextSize(4f);
        //xAxis.setLabelCount(5, true);

        // y??? ??????
        YAxis yLAxis = chart_body.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK); // y??? ????????? ??????

        // y??? ????????? ????????????
        YAxis yRAxis = chart_body.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);


        LineData lineData = new LineData(dataSets);
        chart_body.setData(lineData);

    }

    //????????? ????????????
    public void readDataSteps() {
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {

                        if (dataSet.isEmpty()) {
                            stepTotal = 0;
                        } else {
                            stepTotal = dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                            tv_fitSteps.setText(String.valueOf(stepTotal + "??????"));

                            //????????? ????????? ??????
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(mainData, Context.MODE_PRIVATE);
                            targetSteps = Integer.parseInt(sharedPreferences.getString("targetStep","0"));
                            if(targetSteps == 0){
                                targetSteps = 1;
                            }
                            circleProgressBar.setProgress((stepTotal*100)/targetSteps); //?????? ????????? ?????? ??????
                            //???????????????
                            /*try {
                                saveDatas();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Activity_Chart.this, "FailStep", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //????????? ????????? ????????????
    private void readDataCalories() {
        // ????????? ????????????
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() { //????????? ?????? ???????????????
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        double CaloriesBurn;
                        rounfoffCalories = 0.0;
                        if(dataSet.isEmpty()){
                            CaloriesBurn = 0.0;
                        }
                        else {
                            CaloriesBurn = dataSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat();
                            rounfoffCalories = Math.round(CaloriesBurn * 100.0) / 100.0;
                        }
                        tv_fitCalories.setText(rounfoffCalories+" Kcal");
                        //????????? ??????
                        /*try {
                            saveDatas();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(Activity_Chart.this, "Unabel to Calculate c", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //????????? , ????????? ????????? ???????????? ???????????? ????????? ????????????
    /*private void saveDatas() throws JSONException {

        Log.d("111", "saveDatas: " + stepTotal + "  " + rounfoffCalories);
        SharedPreferences sharedPreferences = this.getSharedPreferences(FitData, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONObject wholedata = new JSONObject(); //??????
        JSONArray Jarray = new JSONArray();
        JSONObject Jdata = new JSONObject();
        Jdata.put("Step",stepTotal);
        Jdata.put("Calories",rounfoffCalories);
        Jarray.put(Jdata);
        wholedata.put(FileName,Jarray);
        editor.putString(FitData,wholedata.toString());
        editor.apply();

    }*/



}