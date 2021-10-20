package com.example.fitpartner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Frag_PictureLog extends Fragment {

    private View view;
    private TextView tv_addbtn;

    private ArrayList<BodyData> bodyDataArrayList;
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


        tv_addbtn = view.findViewById(R.id.textView_addbutton);

        //리사이클러뷰위치 찾아서 연결해주기
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_bodypciture);
        gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        bodyDataArrayList = new ArrayList<>();
        bodyAdapter = new BodyAdapter(bodyDataArrayList);
        recyclerView.setAdapter(bodyAdapter);


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
                /*et_addweight.setHint("몸무게 입력 (Kg)");
                et_addfat.setHint("체지방량 입력 (Kg)");
                et_addproteon.setHint("근골격량 입력 (Kg)");*/

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
                            fat = null;
                            protein = null;
                        }

                        Bitmap bodypicture = bitmap_body;
                        if (bodypicture != null){
                            body = new BodyData(weight,fat,protein,bodypicture,dateData);
                        } else {
                            body = new BodyData(weight,fat,protein,bodypicture,dateData);
                        }

                        bodyDataArrayList.add(body);
                        bodyAdapter.notifyDataSetChanged();
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

}
