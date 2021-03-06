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

    private final String Filename = ((MainActivity)getActivity()).today;
    private final String mainData = "MainData";
    private final String optionData = "OptionData";

    ProgressBar pb_water;

    private View view;
    private TextView tv_water;
    private ImageButton imgbtn_plus;
    private ImageButton imgbtn_minus;
    private ImageView iv_addfood;
    private ImageButton imgbtn_targetWater;
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

        //????????????????????? ???????????? , ??????????????? ??????????????? ?????????????????? ????????? ????????? ?????? ????????????

    }

    //onactivitycreated ??? ???????????? LifecycleObserver??? ????????????.
    //?????? ????????? ??????????????? ???????????????, ????????? ????????? ???????????? ????????? fooddata ???????????? notify????????? ???.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("111", "onCreateView: food");
        view = inflater.inflate(R.layout.frag_foodlog,container,false);



        tv_water = view.findViewById(R.id.textView_water);
        imgbtn_plus = view.findViewById(R.id.imageButton_plus);
        imgbtn_minus = view.findViewById(R.id.imageButton_minus);
        iv_addfood = view.findViewById(R.id.iv_addFood);
        imgbtn_targetWater = view.findViewById(R.id.imageButton_targetWater);
        cb_service = view.findViewById(R.id.checkBox_service);


        //?????????????????? ?????????
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_foodlog);
        gridLayoutManager = new GridLayoutManager(getActivity(),2);


        recyclerView.setLayoutManager(gridLayoutManager);

        foodArray = new ArrayList<>();
        foodAdapter = new FoodAdapter(foodArray, getActivity()); //?????? context??? ????????????
        recyclerView.setAdapter(foodAdapter);
        // ????????????

        //???????????? ????????????
        //??????
        waters = Integer.parseInt(loadWater());
        //??????????????????
        foodArray = loadPreference();
        foodAdapter.setAdapter(foodArray);
        foodAdapter.notifyDataSetChanged();
        //????????????
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(optionData, Context.MODE_PRIVATE);
        String isrunning = sharedPreferences.getString("FGService","0");
        if(isrunning.equals("1")){
            cb_service.setChecked(true);
        } else {
            cb_service.setChecked(false);
        }


        //??????????????????
        pb_water = view.findViewById(R.id.progressBar_inside_water);
        SharedPreferences sharedPreferencesTarget = getActivity().getSharedPreferences(mainData, Context.MODE_PRIVATE);
        String target = sharedPreferencesTarget.getString("targetWater","0");
        int water_target = Integer.parseInt(target);
        pb_water.setMax(water_target);
        pb_water.setProgress(waters);


        //?????????????????????(???????????????) ???????????? ??????
        foodAdapter.setOnClickListener(new FoodAdapter.myRecyclerViewClickListener() {
            @Override
            public void whenItemClick(int position) {
                final FoodData item = foodArray.get(position);
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setIcon(R.mipmap.ic_launcher);
                ad.setTitle("????????????");
                //??????????????? ?????????????????? ??????????????? ?????????
                View dialog_view = inflater.inflate(R.layout.dialog_edit_food,null);
                EditText et_edcalorie = dialog_view.findViewById(R.id.editText_edcalorie);
                EditText et_edprotein = dialog_view.findViewById(R.id.editText_edprotein);

                et_edcalorie.setText(item.getTv_calorie());
                et_edprotein.setText(item.getTv_protein());
                //????????? ???????????? ???????????????
                Editable etext_cal = et_edcalorie.getText();
                Editable etext_pro = et_edprotein.getText();
                Selection.setSelection(etext_cal,etext_cal.length());
                Selection.setSelection(etext_pro,etext_pro.length());

                ad.setView(dialog_view);

                ad.setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                ad.setNegativeButton("??????", new DialogInterface.OnClickListener() {
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

        //????????? ????????????
        cb_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //???????????? ????????????
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
                    //???????????? ?????? ????????? ????????????.
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


        //????????? ?????????(???)
        imgbtn_targetWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setTitle("????????? ???????????? (2000ml -> 2000) ");

                EditText et_targetWater = new EditText(getActivity());
                et_targetWater.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                //????????? ????????? ????????????
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(mainData, Context.MODE_PRIVATE);
                String targetWater = sharedPreferences.getString("targetWater","0");

                if (targetWater != null){
                    //???????????? ????????? ???????????????
                    et_targetWater.setText(targetWater);
                } else {
                    //???????????? ????????? ????????????
                    et_targetWater.setHint("ex) 2L => 2000");
                }

                ad.setView(et_targetWater);

                ad.setPositiveButton("????????????", new DialogInterface.OnClickListener() {
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

        //+?????? ????????????
        imgbtn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waters += 50;
                saveWater();

                pb_water.setProgress(waters);

                if(cb_service.isChecked()){
                    //notification ???????????????
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
                    tv_water.setText("?????? ?????? ???????????? ??????????????????!");
                }

            }
        });

        // -?????? ????????????
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
                    //notification ???????????????
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
                        if (result.getResultCode() == FOOD_RESULT_CODE){ //????????? ????????????
                            Intent intent = result.getData();
                            Bundle bundle = intent.getExtras();
                            if(bundle != null){ //????????? ?????? ?????????
                                //int picture = bundle.getInt("???????????????")
                                String calorie = bundle.getString("calorie");
                                String protein = bundle.getString("protein");
                                String mdate = bundle.getString("date");
                                String mtime = bundle.getString("time");
                                byte[] mbyteArray = bundle.getByteArray("send_byteArray");
                                if (mbyteArray != null){
                                    Bitmap mbitmap = byteArrayToBitmap(mbyteArray);
                                    food = new FoodData(mbitmap,calorie,protein,mdate,mtime,null);
                                } else{ //???????????????
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

    //????????????????????? ????????? ?????? ????????? ??? ?????????
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
        //???????????? ??????????????? ????????????
        for (int i = 0; i < foodArray.size(); i++){
            String path = saveBitmapToPng(foodArray.get(i).getIv_food_picture(),i);
            foodArray.get(i).setBitmapToString(path);
            foodArray.get(i).setIv_food_picture(null);
        }
        String json = gson.toJson(foodArray);
        editor.putString("Foodlog",json);
        editor.apply();
        Log.d("111", "savePreference: ?????? ????????????");

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
            Log.d("111", "loadPreference: ?????? ????????????");
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
        Log.d("111", "saveBitmapToPng: ????????????" + getActivity().getCacheDir() + " ?????? : " + i);
        try{
          tempFile.createNewFile();
          FileOutputStream out = new FileOutputStream(tempFile);
          bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
          out.close();
            Log.d("111", "saveBitmapToJpeg: ????????????");
          return imgpath;
        }catch (Exception e){
            Log.d("111", "saveBitmapToJpeg: ????????????");
            return null;
        }
    }

    /*public Bitmap loadPngtoBitmap(int i){

        try{
            String imgpath = getActivity().getFilesDir() + "/" + "savephoto" + i;
            Bitmap bm = BitmapFactory.decodeFile(imgpath);
            Log.d("111", "loadPngtoBitmap: ????????????");
            return bm;
        }catch (Exception e){
            Log.d("111", "loadPngtoBitmap: ????????????");
            return null;
        }

    }*/



}
