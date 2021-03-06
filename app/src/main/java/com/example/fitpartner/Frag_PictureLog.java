package com.example.fitpartner;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Frag_PictureLog extends Fragment {
    //private final String Filename = ((StaticItem)getActivity().getApplication()).getDate();
    private final String Filename = ((MainActivity)getActivity()).today;
    //private final String Filename = "20211025";
    private final String mainData = "MainData";

    private View view;
    private TextView tv_addbtn;
    private ImageView iv_bestPicture;
    private TextView tv_bestDate;
    private TextView tv_bestWeight;
    private TextView tv_bestMuscle;
    private TextView tv_bestFat;

    private ArrayList<BodyData> bodyDataArrayList;
    private ArrayList<BodyData> bestArray;
    private BodyAdapter bodyAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;

    private Bitmap bitmap_body;
    private BodyData body;
    private String weight;
    private String fat;
    private String protein;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("111", "onCreateView: picture");
        view = inflater.inflate(R.layout.frag_picturelog,container,false);


        tv_addbtn = (TextView)view.findViewById(R.id.textView_addbutton);
        iv_bestPicture = (ImageView)view.findViewById(R.id.imageView_best_picture);
        tv_bestDate = (TextView) view.findViewById(R.id.textView_best_date);
        tv_bestWeight = (TextView) view.findViewById(R.id.textView_best_weight);
        tv_bestMuscle = (TextView) view.findViewById(R.id.textView_best_muscle);
        tv_bestFat = (TextView) view.findViewById(R.id.textView_best_fat);

        //???????????????????????? ????????? ???????????????
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_bodypciture);
        gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        bodyDataArrayList = new ArrayList<>();
        bodyAdapter = new BodyAdapter(bodyDataArrayList,getActivity());
        recyclerView.setAdapter(bodyAdapter);

        //????????? ?????? ????????? ????????? ??????
        bestArray = new ArrayList<>();

        //????????? ????????? ????????????
        bodyDataArrayList = loadPreference();
        bodyAdapter.setAdapter(bodyDataArrayList);
        bodyAdapter.notifyDataSetChanged();

        bestArray = loadBestCondition();
        if(bestArray.size() != 0){
            iv_bestPicture.setImageBitmap(bestArray.get(0).getIv_bodypicture());
            tv_bestDate.setText(bestArray.get(0).getTv_picturedate());
            tv_bestWeight.setText(bestArray.get(0).getTv_totalWeight());
            tv_bestMuscle.setText(bestArray.get(0).getTv_proteinRate());
            tv_bestFat.setText(bestArray.get(0).getTv_fatRate());
        }


        bodyAdapter.setOnClickListener(new BodyAdapter.myRecyclerViewClickListener() {
            @Override
            public void whenItemClick(int position) {
                final BodyData item = bodyDataArrayList.get(position);
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                View dialog_view = inflater.inflate(R.layout.dialog_bodypicture_view,null);
                ImageView iv_bodyExpansion = dialog_view.findViewById(R.id.imageView_expansion);
                Button btn_saveBody = dialog_view.findViewById(R.id.button_saveBody);
                Button btn_bestPick = dialog_view.findViewById(R.id.button_bestPick);


                iv_bodyExpansion.setImageBitmap(item.getIv_bodypicture());
                ad.setView(dialog_view);
                AlertDialog alertDialog = ad.create();
                alertDialog.show();

                btn_saveBody.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //????????? ??????????????? ???????????? ???????????????
                        if (item.getIv_bodypicture() != null){
                            iv_bodyExpansion.setDrawingCacheEnabled(true);
                            Bitmap bitmap = iv_bodyExpansion.getDrawingCache();
                            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "FitPartner_" + Filename, "");
                            Toast.makeText(getActivity(), "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "????????? ????????????.", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }


                        //saveImage((item.getIv_bodypicture()), "FitPartner_" + Filename);


                    }
                });

                btn_bestPick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "????????? ?????? ??????", Toast.LENGTH_SHORT).show();
                        //????????? ????????? array ?????? ??????
                        if(bestArray.size() != 0){
                            bestArray.set(0,item);
                        }
                        else {
                            bestArray.add(item);
                        }
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(mainData, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Gson gson = new GsonBuilder().create();
                        String json = gson.toJson(bestArray);
                        editor.putString("BestBodylog",json);
                        editor.apply();
                        //????????? ???????????? set
                        iv_bestPicture.setImageBitmap(item.getIv_bodypicture());
                        tv_bestDate.setText(item.getTv_picturedate());
                        tv_bestWeight.setText(item.getTv_totalWeight());
                        tv_bestMuscle.setText(item.getTv_proteinRate());
                        tv_bestFat.setText(item.getTv_fatRate());
                        alertDialog.dismiss();
                    }
                });


            }
        });

        //??????????????? ??? ???????????????
        View dialog_view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_picturelog,null,false);
        EditText et_addweight = dialog_view.findViewById(R.id.editText_addweight);
        EditText et_addfat = dialog_view.findViewById(R.id.editText_addfat);
        EditText et_addproteon = dialog_view.findViewById(R.id.editText_addprotein);
        TextView tv_hardfat = dialog_view.findViewById(R.id.textView_hard_adfat);
        TextView tv_hardprotein = dialog_view.findViewById(R.id.textView_hard_adpro);
        CheckBox cb_addinfo = dialog_view.findViewById(R.id.checkBox_addinfo);
        ImageView iv_addbody = dialog_view.findViewById(R.id.imageView_addbody);

        ActivityResultLauncher<Intent> activityLauncher_bodycamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.d("222", "onActivityResult:LAUNCH ");
                        if (result.getResultCode() == getActivity().RESULT_OK){ //????????? ????????????
                            Log.d("222", "onActivityResult:RESULT OK ");
                            Intent cameraIntent = result.getData();
                            bitmap_body = (Bitmap) cameraIntent.getExtras().get("data");
                            if(bitmap_body != null){
                                Log.d("222", "onActivityResult:????????????????????? ");
                                iv_addbody.setImageBitmap(bitmap_body);
                                //?????????????????? ????????????
                                et_addweight.requestFocus();
                                //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
                            }
                        }
                    }
                }
        );

        iv_bestPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("1111", "onClick: ??????");
                        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                        View dialog_view = inflater.inflate(R.layout.dialog_bestbodypicture_view,null);
                        ImageView iv_picture_expansion = dialog_view.findViewById(R.id.imageView_bestpicture_expansion);
                        if (bestArray.size() != 0){
                            iv_picture_expansion.setImageBitmap(bestArray.get(0).getIv_bodypicture());
                            ad.setView(dialog_view);
                            AlertDialog alertDialog = ad.create();
                            alertDialog.show();
                        }
                        else {
                            Toast.makeText(getActivity(), "?????? ????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

        tv_addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //??????????????? ?????? ????????????
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy - MM - dd");

                String dateData = dateformat.format(mDate);


                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                //??????????????? ?????????????????? ??????????????? ?????????
                iv_addbody.setImageResource(R.drawable.ic_baseline_add_circle_outline_24);
                et_addweight.setText(null);
                et_addfat.setText(null);
                et_addproteon.setText(null);


                iv_addbody.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //??????????????? ?????? ?????? ??????
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if(getContext().checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED
                                    && getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                                    && getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==PackageManager.PERMISSION_GRANTED){
                                Log.d("222", "?????? ?????? ??????");
                            }
                            else {
                                Log.d("222", "?????? ?????? ??????");

                                ActivityCompat.requestPermissions(Frag_Activity_Addfood.this,new String[]
                                        {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
                            }
                        }*/


                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        activityLauncher_bodycamera.launch(cameraIntent);
                    }
                });


                //?????? ?????????
                if(cb_addinfo.isChecked()){
                    tv_hardfat.setTextColor(ContextCompat.getColor(getContext(),R.color.black));
                    tv_hardprotein.setTextColor(ContextCompat.getColor(getContext(),R.color.black));
                    et_addfat.setHintTextColor(ContextCompat.getColor(getContext(),R.color.hint));
                    et_addproteon.setHintTextColor(ContextCompat.getColor(getContext(),R.color.hint));
                    et_addfat.setFocusableInTouchMode(true);
                    et_addproteon.setFocusableInTouchMode(true);
                } else {
                    tv_hardfat.setTextColor(ContextCompat.getColor(getContext(),R.color.untouch));
                    tv_hardprotein.setTextColor(ContextCompat.getColor(getContext(),R.color.untouch));
                    et_addfat.setHintTextColor(ContextCompat.getColor(getContext(),R.color.untouch));
                    et_addproteon.setHintTextColor(ContextCompat.getColor(getContext(),R.color.untouch));
                    et_addfat.setFocusableInTouchMode(false);
                    et_addproteon.setFocusableInTouchMode(false);

                }


                cb_addinfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(cb_addinfo.isChecked()){
                            tv_hardfat.setTextColor(ContextCompat.getColor(getContext(),R.color.black));
                            tv_hardprotein.setTextColor(ContextCompat.getColor(getContext(),R.color.black));
                            et_addfat.setHintTextColor(ContextCompat.getColor(getContext(),R.color.hint));
                            et_addproteon.setHintTextColor(ContextCompat.getColor(getContext(),R.color.hint));
                            et_addfat.setFocusableInTouchMode(true);
                            et_addproteon.setFocusableInTouchMode(true);
                        } else {
                            tv_hardfat.setTextColor(ContextCompat.getColor(getContext(),R.color.untouch));
                            tv_hardprotein.setTextColor(ContextCompat.getColor(getContext(),R.color.untouch));
                            et_addfat.setHintTextColor(ContextCompat.getColor(getContext(),R.color.untouch));
                            et_addproteon.setHintTextColor(ContextCompat.getColor(getContext(),R.color.untouch));
                            et_addfat.setFocusableInTouchMode(false);
                            et_addproteon.setFocusableInTouchMode(false);
                        }
                    }
                });
                if(dialog_view.getParent() != null) {
                    ((ViewGroup)dialog_view.getParent()).removeView(dialog_view);
                }
                ad.setView(dialog_view);

                ad.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (cb_addinfo.isChecked()){
                            weight = "????????? : " + et_addweight.getText().toString() + " Kg";
                            fat = "???????????? : " + et_addfat.getText().toString() + " Kg";
                            protein = "???????????? : " + et_addproteon.getText().toString() + " Kg";
                        } else {
                            weight = "????????? : " + et_addweight.getText().toString() + " Kg";
                            fat = "???????????? : " + 0 + " Kg";
                            protein = "???????????? : " +0 + " Kg";
                        }

                        Bitmap bodypicture = bitmap_body;
                        if (bodypicture != null){
                            body = new BodyData(weight,fat,protein,bodypicture,dateData,null,Filename);
                            body.setBodyBitmapToString(saveBitmapToPng(bodypicture));
                        } else {
                            body = new BodyData(weight,fat,protein,null,dateData,null,Filename);
                        }

                        bodyDataArrayList.add(0,body);
                        bodyAdapter.notifyDataSetChanged();

                        savePreference();


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


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void savePreference() {
        //SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Filename, Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences_main = getActivity().getSharedPreferences(mainData, Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences.Editor editor_main = sharedPreferences_main.edit();
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(bodyDataArrayList);
        //editor.putString("Bodylog",json);
        //editor.apply();
        editor_main.putString("Bodylog",json);
        editor_main.apply();

    }




    /*private void savePreference() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new GsonBuilder().create();
        //???????????? ??????????????? ????????????
        for (int i = 0; i < bodyDataArrayList.size(); i++){
            String path = saveBitmapToPng(bodyDataArrayList.get(i).getIv_bodypicture());
            bodyDataArrayList.get(i).setBodyBitmapToString(path);
            bodyDataArrayList.get(i).setIv_bodypicture(null);
        }

        String json = gson.toJson(bodyDataArrayList);
        editor.putString("Bodylog",json);
        editor.apply();
        Log.d("111", "savePreference: ?????? ????????????");

    }*/

    private ArrayList<BodyData> loadPreference(){
        //SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Filename, Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(mainData, Context.MODE_PRIVATE);
        if(sharedPreferences.contains("Bodylog")){
            Gson gson = new GsonBuilder().create();
            String json = sharedPreferences.getString("Bodylog","");
            Type bodylogType = new TypeToken<ArrayList<BodyData>>(){}.getType();
            bodyDataArrayList = gson.fromJson(json,bodylogType);

            for (int i = 0; i < bodyDataArrayList.size(); i++){
                Bitmap bitimg;
                if (bodyDataArrayList.get(i).getBodyBitmapToString() != null){
                    String imgpath = bodyDataArrayList.get(i).getBodyBitmapToString();
                    bitimg = BitmapFactory.decodeFile(imgpath);
                }
                else {
                    bitimg = null;
                }
                bodyDataArrayList.get(i).setIv_bodypicture(bitimg);
            }

            return bodyDataArrayList;
        }
        return bodyDataArrayList;
    }

    private ArrayList<BodyData> loadBestCondition() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(mainData, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("BestBodylog")) {
            Gson gson = new GsonBuilder().create();
            String json_best = sharedPreferences.getString("BestBodylog", "");
            Type bodylogType = new TypeToken<ArrayList<BodyData>>() {}.getType();
            bestArray = gson.fromJson(json_best, bodylogType);

            Bitmap bitimg;
            if (bestArray.get(0).getBodyBitmapToString() != null){
                String imgpath = bestArray.get(0).getBodyBitmapToString();
                bitimg = BitmapFactory.decodeFile(imgpath);
            }
            else {
                bitimg = null;
            }
            bestArray.get(0).setIv_bodypicture(bitimg);

            return bestArray;
        }
        return bestArray;
    }

    public String saveBitmapToPng(Bitmap bitmap) {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddhhmmss");
        String dateData = dateformat.format(mDate);

        File tempFile = new File(getActivity().getFilesDir(),dateData);
        String imgpath = getActivity().getFilesDir() + "/" + dateData;
        try{
            tempFile.createNewFile();
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
            out.close();
            return imgpath;
        }catch (Exception e){
            return null;
        }
    }


    //????????? ??????
    /*public boolean saveImage(Bitmap bitmap, String saveImageName) {
        String saveDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+ "/directoryName";
        File file = new File(saveDir);
        if (!file.exists()) {
            file.mkdir();
        }

        String fileName = saveImageName + ".png";
        File tempFile = new File(saveDir, fileName);
        FileOutputStream output = null;

        try {
            if (tempFile.createNewFile()) {
                output = new FileOutputStream(tempFile);
                // ????????? ?????????
                // ?????? ????????? ??????????????? ????????? ???
                //Bitmap newBitmap = bitmap.createScaledBitmap(bitmap, 200, 200, true);
                // ????????? ??????. ????????? ????????? output stream??? ??????. 2?????? ????????? ??????????????? 100?????? ?????? ?????? ?????????..
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            } else {
                // ?????? ????????? ?????? ??????
                Log.d("TEST_LOG", "?????? ????????? ?????? ??????:"+saveImageName);

                return false;
            }
        } catch (FileNotFoundException e) {
            Log.d("TEST_LOG", "????????? ?????? ??? ??????");
            return false;

        } catch (IOException e) {
            Log.d("TEST_LOG", "IO ??????");
            e.printStackTrace();
            return false;

        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }*/



}
