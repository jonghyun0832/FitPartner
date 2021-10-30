package com.example.fitpartner;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Frag_Progress extends Fragment {

    private View view;
    private ArrayList<FoodData> foodArray;
    private ArrayList<BodyData> bodyArray;
    private ArrayList<WorkoutData> workoutArray;

    private final String mainData = "MainData";
    //private int total_calorie;
    //Integer.parseInt(((MainActivity)getActivity()).today)

    //베너 인덱스용
    private int tmp_index = 0;
    boolean isRunning;
    Handler handler;
    TextView tv_progressBanner;
    MRunnable runnable;
    Thread thread;
    String[] myArr = {"비만의 괴로움이 운동의 괴로움보다 크다", "마법의 약은 없다", "시도하지않으면 변하는건없다",
    "운동에 시간을 내지 않으면 병때문에 시간을 내야 될지도 모른다","고통은 지나간다, 아름다움은 남는다"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("111", "onCreateView: progress");
        view = inflater.inflate(R.layout.frag_progress, container, false);


        CalendarView cv_log = view.findViewById(R.id.calendarView_logs);

        long totay_date = cv_log.getDate() - 1;
        cv_log.setDate(totay_date);


        //운동기록
        TextView tv_logExdata = view.findViewById(R.id.textView_log_exdata);
        TextView tv_logTime = view.findViewById(R.id.textView_log_time);

        //건강상태
        TextView tv_logWeight = view.findViewById(R.id.textView_log_weight);
        TextView tv_logProteinRate = view.findViewById(R.id.textView_log_proteinrate);
        TextView tv_logFatRate = view.findViewById(R.id.textView_log_fatrate);

        //식습관
        TextView tv_logCalorie = view.findViewById(R.id.textView_log_calorie);
        TextView tv_logFoodProtein = view.findViewById(R.id.textView_log_foodprotein);
        TextView tv_logwater = view.findViewById(R.id.textView_log_water);

        //더보기 버튼들
        TextView tv_moreWorkout = view.findViewById(R.id.textView_more_workout);
        TextView tv_moreBodylog = view.findViewById(R.id.textView_more_bodylog);
        TextView tv_moreFoodlog = view.findViewById(R.id.textView_more_foodlog);

        //배너등록
        tv_progressBanner = view.findViewById(R.id.textView_ProgressBanner);
        tv_progressBanner.setText(myArr[tmp_index]);
        handler = new Handler();
        runnable = new MRunnable();

        isRunning = true;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRunning){
                    try {
                        Thread.sleep(3000);
                        handler.post(runnable);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        thread.start();




        //초기세팅
        {
            foodArray = new ArrayList<>();
            workoutArray = new ArrayList<>();
            bodyArray = new ArrayList<>();

            //데이터 저장용 변수들
            //운동기록
            String exerciseData = ""; //운동 내용
            int totalTime = 0;

            //건강상태
            int total_weight = 0; //몸무게
            int total_protein = 0; //근육
            int total_fat = 0; //체지방

            //식습관 기록
            int total_calorie = 0; //총 섭취 칼로리
            int total_foodprotein = 0; //총 섭취 단백질

            int[] today = getDate(cv_log);

            //문자열 형태로 변환하여 데이터 저장
            String str_year = today[0] + "";
            String str_month = today[1] + "";
            String str_day = today[2] + "";
            if (today[1] < 10) {
                str_month = "0" + today[1];
            }
            String str_date = str_year + str_month + str_day;

            //운동기록 가져오기
            workoutArray = loadWorkoutPreference();
            if (workoutArray.size() != 0) {
                for (int i = 0; i < workoutArray.size(); i++) {
                    if (workoutArray.get(i).getCreateDate().equals(str_date)) {
                        //운동기록
                        exerciseData += "\n" + workoutArray.get(i).getTv_exData();
                        //총 운동시간
                        String[] startTimeArray = workoutArray.get(i).getTv_startTime().split(" : ");
                        String[] endTimeArray = workoutArray.get(i).getTv_endTime().split(" : ");
                        totalTime += (Integer.parseInt(endTimeArray[0]) - Integer.parseInt(startTimeArray[0])) * 60 +
                                Integer.parseInt(endTimeArray[1]) - Integer.parseInt(startTimeArray[1]);

                    }
                }
                //운동시간 텍스트 설정
                if (totalTime == 0) { //운동기록(시간)이 없을때
                    tv_logTime.setText("기록없음");
                } else {
                    tv_logTime.setText("총 운동 시간 : "  + totalTime / 60 + " 시간" + totalTime % 60 + " 분");
                }
                //운동기록 텍스트 설정
                if (exerciseData != "") {
                    tv_logExdata.setText("운동기록 : " + exerciseData);
                } else {
                    tv_logExdata.setText("기록없음");
                }
            } else { //저장된 데이터가 없을떄 (완전처음)
                tv_logExdata.setText("기록없음");
                tv_logTime.setText("기록없음");
            }


            //몸상태기록 가져오기
            bodyArray = loadBodyPreference();
            if (bodyArray.size() != 0) {
                int tmp_weight = 0; //몸무게 더해주기용
                int tmp_protein = 0; //근골격량 더해주기용
                int tmp_fat = 0; //체지방량 더해주기용
                int cnt = 0; //평균내기용
                for (int i = 0; i < bodyArray.size(); i++) {
                    if (bodyArray.get(i).getCreateDate().equals(str_date)) {
                        //몸무게,근골격량, 체지방량 평균내기
                        String[] weightarray = bodyArray.get(i).getTv_totalWeight().split(" ");
                        String[] proteinarray = bodyArray.get(i).getTv_proteinRate().split(" ");
                        String[] fatarray = bodyArray.get(i).getTv_fatRate().split(" ");
                        tmp_weight += Integer.parseInt(weightarray[2]);
                        tmp_protein += Integer.parseInt(proteinarray[2]);
                        tmp_fat += Integer.parseInt(fatarray[2]);
                        cnt += 1;
                        total_weight = tmp_weight / cnt;
                        total_protein = tmp_protein / cnt;
                        total_fat = tmp_fat / cnt;

                    }
                }
                if (total_protein == 0) {
                    tv_logProteinRate.setText("기록없음");
                } else {
                    tv_logProteinRate.setText("평균 근골격률 : " + String.format("%.2f", (float) (total_protein * 100) / total_weight) + " %");
                }
                if (total_fat == 0) {
                    tv_logFatRate.setText("기록없음");
                } else {
                    tv_logFatRate.setText("평균 체지방률 : " + String.format("%.2f", (float) (total_fat * 100) / total_weight) + " %");
                }
                if (total_weight == 0) {
                    tv_logWeight.setText("기록없음");
                } else {
                    tv_logWeight.setText("평균 몸무게 : " + total_weight + "Kg");
                }
            } else { //저장된 데이터가 없을떄 (완전처음)
                tv_logWeight.setText("기록없음");
            }


            //음식기록 가져오기
            foodArray = loadFoodPreference(str_date);
            if (foodArray.size() != 0) {
                for (int i = 0; i < foodArray.size(); i++) {
                    //데이터 가져와서 리스트의 크기만큼 안에있는 데이터들 가져와서 더해주기
                    total_calorie += Integer.parseInt(foodArray.get(i).getTv_calorie());
                    total_foodprotein += Integer.parseInt(foodArray.get(i).getTv_protein());
                }
                tv_logCalorie.setText("섭취한 총 칼로리 : " + total_calorie + "Kcal");
                tv_logFoodProtein.setText("섭취한 총 단백질 : " + total_foodprotein + " g");
            } else {
                tv_logCalorie.setText("기록없음");
                tv_logFoodProtein.setText("기록없음");
            }

            //섭취한 물 기록
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(mainData, Context.MODE_PRIVATE);
            String targetWater = sharedPreferences.getString("targetWater","0");
            String water = loadWater(str_date);
            tv_logwater.setText("섭취한 수분 : " + water + " / " + targetWater +  " mL");
        }


        //더보기 버튼 눌렀을때 하단 네비게이션변환과 함께 프래그먼트 전환
        tv_moreWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).setFrag(1);
                ((MainActivity) getActivity()).bottomNavigationView.setSelectedItemId(R.id.action_workoutLog);
            }
        });
        tv_moreBodylog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).setFrag(2);
                ((MainActivity) getActivity()).bottomNavigationView.setSelectedItemId(R.id.action_pictureLog);
            }
        });
        tv_moreFoodlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).setFrag(3);
                ((MainActivity) getActivity()).bottomNavigationView.setSelectedItemId(R.id.action_foodLog);
            }
        });

        //달력에서 날짜 바뀔떄 세팅 변환
        cv_log.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                foodArray = new ArrayList<>();
                workoutArray = new ArrayList<>();
                bodyArray = new ArrayList<>();

                //데이터 저장용 변수들
                //운동기록
                String exerciseData = ""; //운동 내용
                int totalTime = 0;

                //건강상태
                int total_weight = 0; //몸무게
                int total_protein = 0; //근육
                int total_fat = 0; //체지방

                //식습관 기록
                int total_calorie = 0; //총 섭취 칼로리
                int total_foodprotein = 0; //총 섭취 단백질


                //문자열 형태로 변환하여 데이터 저장
                String str_year = year + "";
                String str_month = month + 1 + "";
                String str_day = day + "";
                if (month + 1 < 10) {
                    str_month = "0" + (month + 1);
                }
                String str_date = str_year + str_month + str_day;

                //운동기록 가져오기
                workoutArray = loadWorkoutPreference();
                if (workoutArray.size() != 0) {
                    for (int i = 0; i < workoutArray.size(); i++) {
                        if (workoutArray.get(i).getCreateDate().equals(str_date)) {
                            //운동기록
                            exerciseData += "\n" + workoutArray.get(i).getTv_exData();
                            //총 운동시간
                            String[] startTimeArray = workoutArray.get(i).getTv_startTime().split(" : ");
                            String[] endTimeArray = workoutArray.get(i).getTv_endTime().split(" : ");
                            totalTime += (Integer.parseInt(endTimeArray[0]) - Integer.parseInt(startTimeArray[0])) * 60 +
                                    Integer.parseInt(endTimeArray[1]) - Integer.parseInt(startTimeArray[1]);

                        }
                    }
                    //운동시간 텍스트 설정
                    if (totalTime == 0) { //운동기록(시간)이 없을때
                        tv_logTime.setText("기록없음");
                    } else {
                        tv_logTime.setText("총 운동 시간 : "  + totalTime / 60 + " 시간" + totalTime % 60 + " 분");
                    }
                    //운동기록 텍스트 설정
                    if (exerciseData != "") {
                        tv_logExdata.setText("운동기록 : " + exerciseData);
                    } else {
                        tv_logExdata.setText("기록없음");
                    }
                } else { //저장된 데이터가 없을떄 (완전처음)
                    tv_logExdata.setText("기록없음");
                    tv_logTime.setText("기록없음");
                }


                //몸상태기록 가져오기
                bodyArray = loadBodyPreference();
                if (bodyArray.size() != 0) {
                    int tmp_weight = 0; //몸무게 더해주기용
                    int tmp_protein = 0; //근골격량 더해주기용
                    int tmp_fat = 0; //체지방량 더해주기용
                    int cnt = 0; //평균내기용
                    for (int i = 0; i < bodyArray.size(); i++) {
                        if (bodyArray.get(i).getCreateDate().equals(str_date)) {
                            //몸무게,근골격량, 체지방량 평균내기
                            String[] weightarray = bodyArray.get(i).getTv_totalWeight().split(" ");
                            String[] proteinarray = bodyArray.get(i).getTv_proteinRate().split(" ");
                            String[] fatarray = bodyArray.get(i).getTv_fatRate().split(" ");
                            tmp_weight += Integer.parseInt(weightarray[2]);
                            tmp_protein += Integer.parseInt(proteinarray[2]);
                            tmp_fat += Integer.parseInt(fatarray[2]);
                            cnt += 1;
                            total_weight = tmp_weight / cnt;
                            total_protein = tmp_protein / cnt;
                            total_fat = tmp_fat / cnt;

                        }
                    }
                    if (total_protein == 0) {
                        tv_logProteinRate.setText("기록없음");
                    } else {
                        tv_logProteinRate.setText("평균 근골격률 : " + String.format("%.2f", (float) (total_protein * 100) / total_weight) + " %");
                    }
                    if (total_fat == 0) {
                        tv_logFatRate.setText("기록없음");
                    } else {
                        tv_logFatRate.setText("평균 체지방률 : " + String.format("%.2f", (float) (total_fat * 100) / total_weight) + " %");
                    }
                    if (total_weight == 0) {
                        tv_logWeight.setText("기록없음");
                    } else {
                        tv_logWeight.setText("평균 몸무게 : " + total_weight + "Kg");
                    }
                } else { //저장된 데이터가 없을떄 (완전처음)
                    tv_logWeight.setText("기록없음");
                }


                //음식기록 가져오기
                foodArray = loadFoodPreference(str_date);
                if (foodArray.size() != 0) {
                    for (int i = 0; i < foodArray.size(); i++) {
                        //데이터 가져와서 리스트의 크기만큼 안에있는 데이터들 가져와서 더해주기
                        total_calorie += Integer.parseInt(foodArray.get(i).getTv_calorie());
                        total_foodprotein += Integer.parseInt(foodArray.get(i).getTv_protein());
                    }
                    tv_logCalorie.setText("섭취한 총 칼로리 : " + total_calorie + "Kcal");
                    tv_logFoodProtein.setText("섭취한 총 단백질 : " + total_foodprotein + " g");
                } else {
                    tv_logCalorie.setText("기록없음");
                    tv_logFoodProtein.setText("기록없음");
                }

                //섭취한 물 기록
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(mainData, Context.MODE_PRIVATE);
                String targetWater = sharedPreferences.getString("targetWater","0");
                String water = loadWater(str_date);
                tv_logwater.setText("섭취한 수분 : " + water + " / " + targetWater +  " mL");


            }
        });

        return view;
    }


    private ArrayList<FoodData> loadFoodPreference(String str_date) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(str_date, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Foodlog")) {
            Log.d("1111", "loadFoodPreference: 여긴오는건가?");
            Gson gson = new GsonBuilder().create();
            String json = sharedPreferences.getString("Foodlog", "");
            Type foodlogType = new TypeToken<ArrayList<FoodData>>() {
            }.getType();
            foodArray = gson.fromJson(json, foodlogType);

            for (int i = 0; i < foodArray.size(); i++) {
                Bitmap bitimg;
                if (foodArray.get(i).getBitmapToString() != null) {
                    String imgpath = foodArray.get(i).getBitmapToString();
                    bitimg = BitmapFactory.decodeFile(imgpath);
                } else {
                    bitimg = null;
                }
                foodArray.get(i).setIv_food_picture(bitimg);
            }
            return foodArray;
        }
        return foodArray;
    }

    private ArrayList<WorkoutData> loadWorkoutPreference() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(mainData, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Workoutlog")) {
            Gson gson = new GsonBuilder().create();
            String json = sharedPreferences.getString("Workoutlog", "");
            Type workoutlogType = new TypeToken<ArrayList<WorkoutData>>() {
            }.getType();
            workoutArray = gson.fromJson(json, workoutlogType);
            return workoutArray;
        }
        return workoutArray;
    }

    private ArrayList<BodyData> loadBodyPreference() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(mainData, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Bodylog")) {
            Gson gson = new GsonBuilder().create();
            String json = sharedPreferences.getString("Bodylog", "");
            Type bodylogType = new TypeToken<ArrayList<BodyData>>() {
            }.getType();
            bodyArray = gson.fromJson(json, bodylogType);

            for (int i = 0; i < foodArray.size(); i++) {
                Bitmap bitimg;
                if (foodArray.get(i).getBitmapToString() != null) {
                    String imgpath = foodArray.get(i).getBitmapToString();
                    bitimg = BitmapFactory.decodeFile(imgpath);
                } else {
                    bitimg = null;
                }
                foodArray.get(i).setIv_food_picture(bitimg);
            }


            return bodyArray;
        }
        return bodyArray;
    }

    private String loadWater(String str_date) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(str_date, Context.MODE_PRIVATE);
        String water = sharedPreferences.getString("Water", "0");
        return water;
    }

    public int[] getDate(CalendarView cv) {
        long time = cv.getDate();
        Date d1 = new Date(time);
        Calendar c = Calendar.getInstance();
        c.setTime(d1);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        int[] datearray = new int[]{year, month, day};
        return datearray;
    }

    //인터페이스로 runnable구현
    public class MRunnable implements Runnable {
        @Override
        public void run() {
            //int i=0;
            //bar.incrementProgressBy(5);
            if (tmp_index < myArr.length - 1){
                tmp_index += 1;
                tv_progressBanner.setText(myArr[tmp_index]);
            }
            else {
                tmp_index = 0;
                tv_progressBanner.setText(myArr[tmp_index]);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isRunning = false;
        thread.interrupt();
    }
}
