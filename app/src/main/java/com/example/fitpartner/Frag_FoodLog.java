package com.example.fitpartner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Frag_FoodLog extends Fragment {

    //private final String Filename = ((StaticItem)getActivity().getApplication()).getDate();
    private final String Filename = ((MainActivity)getActivity()).today;
    private final String mainData = "MainData";
    private final String optionData = "OptionData";
    //private final String Filename = "20211025";

    ProgressBar pb_water;


    private View view;
    private TextView tv_water;
    private ImageButton imgbtn_plus;
    private ImageButton imgbtn_minus;
    private ImageView iv_addfood;
    private Button btn_targetWater;
    public int waters;
    private CheckBox cb_service;

    public int getWaters() {
        return waters;
    }

    private ArrayList<FoodData> foodArray;
    private FoodAdapter foodAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;



    private static final int FOOD_RESULT_CODE = 5050;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //초기화해야하는 리소스들 , 프래그먼트 생성하면서 넘겨준값들이 있으면 여기서 변수 넣어주기

    }

    //onactivitycreated 는 없어졌고 LifecycleObserver를 써야된다.
    //이제 어답터 끝났으니까 매니저설정, 껴줄거 껴주고 리스트에 데이터 fooddata 넣어주고 notify시키면 끝.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("111", "onCreateView: food");
        view = inflater.inflate(R.layout.frag_foodlog,container,false);

        tv_water = view.findViewById(R.id.textView_water);
        imgbtn_plus = view.findViewById(R.id.imageButton_plus);
        imgbtn_minus = view.findViewById(R.id.imageButton_minus);
        iv_addfood = view.findViewById(R.id.iv_addFood);
        btn_targetWater = view.findViewById(R.id.button_targetWater);
        cb_service = view.findViewById(R.id.checkBox_service);


        //리사이클러뷰 만들기
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_foodlog);
        gridLayoutManager = new GridLayoutManager(getActivity(),2);


        recyclerView.setLayoutManager(gridLayoutManager);

        foodArray = new ArrayList<>();
        foodAdapter = new FoodAdapter(foodArray, getActivity()); //아직 context는 안넘겨줌
        recyclerView.setAdapter(foodAdapter);
        // 설정완료

        //저장된거 불러오기
        //수분
        waters = Integer.parseInt(loadWater());
        //리사이클러뷰
        foodArray = loadPreference();
        foodAdapter.setAdapter(foodArray);
        foodAdapter.notifyDataSetChanged();
        //체크박스
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(optionData, Context.MODE_PRIVATE);
        String isrunning = sharedPreferences.getString("FGService","0");
        if(isrunning.equals("1")){
            cb_service.setChecked(true);
        } else {
            cb_service.setChecked(false);
        }


        //프로그래스바
        pb_water = view.findViewById(R.id.progressBar_inside_water);
        SharedPreferences sharedPreferencesTarget = getActivity().getSharedPreferences(mainData, Context.MODE_PRIVATE);
        String target = sharedPreferencesTarget.getString("targetWater","0");
        int water_target = Integer.parseInt(target);
        pb_water.setMax(water_target);
        pb_water.setProgress(waters);


        //온클릭구현한거(어답터먼저) 메인에서 사용
        foodAdapter.setOnClickListener(new FoodAdapter.myRecyclerViewClickListener() {
            @Override
            public void whenItemClick(int position) {
                final FoodData item = foodArray.get(position);
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setIcon(R.mipmap.ic_launcher);
                ad.setTitle("수정하기");
                //다이얼로그 레이아웃이랑 연결해주는 부분임
                View dialog_view = inflater.inflate(R.layout.dialog_edit_food,null);
                EditText et_edcalorie = dialog_view.findViewById(R.id.editText_edcalorie);
                EditText et_edprotein = dialog_view.findViewById(R.id.editText_edprotein);

                et_edcalorie.setText(item.getTv_calorie());
                et_edprotein.setText(item.getTv_protein());
                //선택시 포커스가 끝으로가게
                Editable etext_cal = et_edcalorie.getText();
                Editable etext_pro = et_edprotein.getText();
                Selection.setSelection(etext_cal,etext_cal.length());
                Selection.setSelection(etext_pro,etext_pro.length());

                ad.setView(dialog_view);

                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String result_calorie = et_edcalorie.getText().toString();
                        String result_protein = et_edprotein.getText().toString();
                        item.setTv_calorie(result_calorie);
                        item.setTv_protein(result_protein);
                        foodAdapter.notifyItemChanged(position);
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
                et_edcalorie.requestFocus();
            }
        });

        //서비스 실행하기
        cb_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //체크상태 저장하기
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(optionData, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String isrunning;
                if (cb_service.isChecked() == true){
                    isrunning = "1";
                } else {
                    isrunning = "0";
                }
                editor.putString("FGService",isrunning);
                editor.apply();


                if(cb_service.isChecked() == true){
                    Intent intent = new Intent(getContext(), WaterService.class);
                    //서비스에 오늘 날짜를 넘겨준다.
                    intent.putExtra("date",Filename);
                    intent.setAction("startForeground");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(intent);
                        Log.d("111", "onClick: ");
                    } else{
                        getActivity().startService(intent);
                    }
                } else {
                    Intent intent = new Intent(getContext(), WaterService.class);
                    intent.setAction("stopForeground");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(intent);
                        Log.d("111", "onClick: ");
                    } else{
                        getActivity().startService(intent);
                    }
                }
            }
        });


        //목표량 정하기(물)
        btn_targetWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());

                ad.setTitle("목표량 설정하기 (2000ml -> 2000) ");

                EditText et_targetWater = new EditText(getActivity());
                et_targetWater.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                //저장된 목표량 가져오기
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(mainData, Context.MODE_PRIVATE);
                String targetWater = sharedPreferences.getString("targetWater","0");

                if (targetWater != null){
                    //데이터가 있으면 표시해주고
                    et_targetWater.setText(targetWater);
                } else {
                    //데이터가 없으면 힌트표시
                    et_targetWater.setHint("ex) 2L => 2000");
                }

                ad.setView(et_targetWater);

                ad.setPositiveButton("설정하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(mainData, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String targetWater = et_targetWater.getText().toString();
                        editor.putString("targetWater",targetWater);
                        editor.apply();
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
        });

        //+버튼 눌렀을때
        imgbtn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waters += 50;
                saveWater();

                pb_water.setProgress(waters);

                if(cb_service.isChecked()){
                    //notification 업데이트용
                    Intent intent = new Intent(getContext(), WaterService.class);
                    intent.putExtra("change","1");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(intent);
                        Log.d("111", "onClick: ");
                    }
                }
                if (waters < water_target){
                    tv_water.setText(waters + " mL");
                } else {
                    tv_water.setText("목표 수분 섭취량을 달성했습니다!");
                }

            }
        });

        // -버튼 눌렀을때
        imgbtn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (waters == 0){
                    waters = 0;
                } else {
                    waters -= 50;
                }
                tv_water.setText(waters + " mL");
                saveWater();

                pb_water.setProgress(waters);

                if(cb_service.isChecked() == true){
                    //notification 업데이트용
                    Intent intent = new Intent(getContext(), WaterService.class);
                    intent.putExtra("change","1");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(intent);
                        Log.d("111", "onClick: ");
                    }
                }

            }
        });


        ActivityResultLauncher<Intent> activityLauncher_fdadd = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        FoodData food;
                        if (result.getResultCode() == FOOD_RESULT_CODE){ //코드가 맞을경우
                            Intent intent = result.getData();
                            Bundle bundle = intent.getExtras();
                            if(bundle != null){ //번들에 값이 있으면
                                //int picture = bundle.getInt("사진데이터")
                                String calorie = bundle.getString("calorie");
                                String protein = bundle.getString("protein");
                                String mdate = bundle.getString("date");
                                String mtime = bundle.getString("time");
                                byte[] mbyteArray = bundle.getByteArray("send_byteArray");
                                if (mbyteArray != null){
                                    Bitmap mbitmap = byteArrayToBitmap(mbyteArray);
                                    food = new FoodData(mbitmap,calorie,protein,mdate,mtime,null);
                                } else{ //사진없을때
                                    food = new FoodData(null,calorie,protein,mdate,mtime,null);
                                }


                                foodArray.add(0,food);
                                foodAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
        );


        iv_addfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Frag_Activity_Addfood.class);
                activityLauncher_fdadd.launch(intent);

            }
        });

        tv_water.setText(waters + " mL");



        return view;
    }

    //포그라운드에서 늘려도 함께 늘어날 수 있도록
    @Override
    public void onResume() {
        super.onResume();
        waters = Integer.parseInt(loadWater());
        tv_water.setText(waters + "mL");
    }

    public Bitmap byteArrayToBitmap(byte[] byteArray){
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        return bitmap;
    }


    private void saveWater() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String water = Integer.toString(waters);
        editor.putString("Water",water);
        editor.apply();
    }

    private String loadWater() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Filename, Context.MODE_PRIVATE);
        String water = sharedPreferences.getString("Water","0");
        return water;
    }


    private void savePreference() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new GsonBuilder().create();
        //비트맵만 변환해주고 다시넣기
        for (int i = 0; i < foodArray.size(); i++){
            String path = saveBitmapToPng(foodArray.get(i).getIv_food_picture(),i);
            foodArray.get(i).setBitmapToString(path);
            foodArray.get(i).setIv_food_picture(null);
        }
        String json = gson.toJson(foodArray);
        editor.putString("Foodlog",json);
        editor.apply();
        Log.d("111", "savePreference: 일단 저장했음");

    }


    private ArrayList<FoodData> loadPreference(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Filename, Context.MODE_PRIVATE);
        if(sharedPreferences.contains("Foodlog")){
            Gson gson = new GsonBuilder().create();
            String json = sharedPreferences.getString("Foodlog","");
            Type foodlogType = new TypeToken<ArrayList<FoodData>>(){}.getType();
            foodArray = gson.fromJson(json,foodlogType);

            for (int i = 0; i < foodArray.size(); i++){
                Bitmap bitimg;
                if (foodArray.get(i).getBitmapToString() != null){
                    String imgpath = foodArray.get(i).getBitmapToString();
                    bitimg = BitmapFactory.decodeFile(imgpath);
                }
                else {
                    bitimg = null;
                }
                //Bitmap bitimg = loadPngtoBitmap(i);
                foodArray.get(i).setIv_food_picture(bitimg);
            }

            Log.d("111", "loadPreference: " + foodArray.size());
            Log.d("111", "loadPreference: 일단 불러왔음");
            return foodArray;
        }
        return foodArray;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        savePreference();
    }


    public String saveBitmapToPng(Bitmap bitmap, int i) {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
        String dateData = dateformat.format(mDate);


        File tempFile = new File(getActivity().getFilesDir(),dateData + "savephoto" + i);
        String imgpath = getActivity().getFilesDir() + "/" + dateData + "savephoto" + i;
        Log.d("111", "saveBitmapToPng: 파일위치" + getActivity().getCacheDir() + " 번호 : " + i);
        try{
          tempFile.createNewFile();
          FileOutputStream out = new FileOutputStream(tempFile);
          bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
          out.close();
            Log.d("111", "saveBitmapToJpeg: 저장성공");
          return imgpath;
        }catch (Exception e){
            Log.d("111", "saveBitmapToJpeg: 저장실패");
            return null;
        }
    }

    /*public Bitmap loadPngtoBitmap(int i){

        try{
            String imgpath = getActivity().getFilesDir() + "/" + "savephoto" + i;
            Bitmap bm = BitmapFactory.decodeFile(imgpath);
            Log.d("111", "loadPngtoBitmap: 로드성공");
            return bm;
        }catch (Exception e){
            Log.d("111", "loadPngtoBitmap: 로드실패");
            return null;
        }

    }*/



}
