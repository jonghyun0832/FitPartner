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

        //리사이클러뷰위치 찾아서 연결해주기
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_bodypciture);
        gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        bodyDataArrayList = new ArrayList<>();
        bodyAdapter = new BodyAdapter(bodyDataArrayList,getActivity());
        recyclerView.setAdapter(bodyAdapter);

        //최고의 사진 선정용 리스트 생성
        bestArray = new ArrayList<>();

        //저장된 데이터 불러오기
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

                        //사진이 존재할떄만 갤러리에 저장시키기
                        if (item.getIv_bodypicture() != null){
                            iv_bodyExpansion.setDrawingCacheEnabled(true);
                            Bitmap bitmap = iv_bodyExpansion.getDrawingCache();
                            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "FitPartner_" + Filename, "");
                            Toast.makeText(getActivity(), "갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "사진이 없습니다.", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }


                        //saveImage((item.getIv_bodypicture()), "FitPartner_" + Filename);


                    }
                });

                btn_bestPick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "최고의 사진 등록", Toast.LENGTH_SHORT).show();
                        //저장용 데이터 array 하나 세팅
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
                        //나머지 데이터들 set
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

        //다이얼로그 뷰 만들어놓기
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
                        if (result.getResultCode() == getActivity().RESULT_OK){ //코드가 맞을경우
                            Log.d("222", "onActivityResult:RESULT OK ");
                            Intent cameraIntent = result.getData();
                            bitmap_body = (Bitmap) cameraIntent.getExtras().get("data");
                            if(bitmap_body != null){
                                Log.d("222", "onActivityResult:비트맵안비었음 ");
                                iv_addbody.setImageBitmap(bitmap_body);
                                //다음항목으로 자동이동
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
                        Log.d("1111", "onClick: 만듬");
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
                            Toast.makeText(getActivity(), "먼저 최고의 사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

        tv_addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //클릭했을떄 시간 가져오기
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy - MM - dd");

                String dateData = dateformat.format(mDate);


                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                //다이얼로그 레이아웃이랑 연결해주는 부분임
                iv_addbody.setImageResource(R.drawable.ic_baseline_add_circle_outline_24);
                et_addweight.setText(null);
                et_addfat.setText(null);
                et_addproteon.setText(null);


                iv_addbody.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //권한끝냈고 이제 사진 찍기
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if(getContext().checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED
                                    && getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                                    && getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==PackageManager.PERMISSION_GRANTED){
                                Log.d("222", "권한 설정 완료");
                            }
                            else {
                                Log.d("222", "권한 설정 요청");

                                ActivityCompat.requestPermissions(Frag_Activity_Addfood.this,new String[]
                                        {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
                            }
                        }*/


                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        activityLauncher_bodycamera.launch(cameraIntent);
                    }
                });


                //기본 추가시
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

                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (cb_addinfo.isChecked()){
                            weight = "몸무게 : " + et_addweight.getText().toString() + " Kg";
                            fat = "체지방량 : " + et_addfat.getText().toString() + " Kg";
                            protein = "근골격량 : " + et_addproteon.getText().toString() + " Kg";
                        } else {
                            weight = "몸무게 : " + et_addweight.getText().toString() + " Kg";
                            fat = "체지방량 : " + 0 + " Kg";
                            protein = "근골격량 : " +0 + " Kg";
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
        //비트맵만 변환해주고 다시넣기
        for (int i = 0; i < bodyDataArrayList.size(); i++){
            String path = saveBitmapToPng(bodyDataArrayList.get(i).getIv_bodypicture());
            bodyDataArrayList.get(i).setBodyBitmapToString(path);
            bodyDataArrayList.get(i).setIv_bodypicture(null);
        }

        String json = gson.toJson(bodyDataArrayList);
        editor.putString("Bodylog",json);
        editor.apply();
        Log.d("111", "savePreference: 일단 저장했음");

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


    //이미지 저장
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
                // 이미지 줄이기
                // 사진 비율로 압축하도록 수정할 것
                //Bitmap newBitmap = bitmap.createScaledBitmap(bitmap, 200, 200, true);
                // 이미지 압축. 압축된 파일은 output stream에 저장. 2번째 인자는 압축률인데 100으로 해도 많이 깨진다..
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            } else {
                // 같은 이름의 파일 존재
                Log.d("TEST_LOG", "같은 이름의 파일 존재:"+saveImageName);

                return false;
            }
        } catch (FileNotFoundException e) {
            Log.d("TEST_LOG", "파일을 찾을 수 없음");
            return false;

        } catch (IOException e) {
            Log.d("TEST_LOG", "IO 에러");
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
