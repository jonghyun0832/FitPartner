package com.example.fitpartner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.BufferUnderflowException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Frag_Activity_Addfood extends AppCompatActivity {

    private ImageView iv_addPicture;
    private EditText et_calorie;
    private EditText et_protein;
    private Button btn_fdCancel;
    private Button btn_fdAdd;

    byte[] send_byteArray;


    //코드용
    private static final int FOOD_RESULT_CODE = 5050;

    //시간설정
    long now = System.currentTimeMillis();
    Date mDate = new Date(now);
    SimpleDateFormat timeformat = new SimpleDateFormat("HH : mm");
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy - MM - dd");

    String timeData = timeformat.format(mDate);
    String dateData = dateformat.format(mDate);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frag_addfood);

        iv_addPicture = (ImageView)findViewById(R.id.imageView_addpicture);
        btn_fdCancel = (Button)findViewById(R.id.button_fdcancel);
        btn_fdAdd = (Button)findViewById(R.id.button_fdadd);
        et_calorie = (EditText)findViewById(R.id.editText_calorie);
        et_protein = (EditText)findViewById(R.id.editText_protein);

        //인풋메소드매니저 설정 - 포커스 옮기기에 사용할거임
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        ActivityResultLauncher<Intent> activityLauncher_foodcamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.d("222", "onActivityResult:LAUNCH ");
                        if (result.getResultCode() == RESULT_OK){ //코드가 맞을경우
                            Log.d("222", "onActivityResult:RESULT OK ");
                            Intent cameraIntent = result.getData();
                            Bitmap bitmap_food = (Bitmap) cameraIntent.getExtras().get("data");
                            if(bitmap_food != null){
                                Log.d("222", "onActivityResult:비트맵안비었음 ");
                                send_byteArray = bitmapToByteArray(bitmap_food);
                                iv_addPicture.setImageBitmap(bitmap_food);
                                //다음항목으로 자동이동
                                et_calorie.requestFocus();
                                imm.showSoftInput(et_calorie,InputMethodManager.SHOW_IMPLICIT);
                                //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
                            }
                        }
                    }
                }
        );

        //돌아가기 클릭시 액티비티 종료
        btn_fdCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_fdAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                //bundle.putInt("비트맵바꾼사진");
                bundle.putString("calorie",et_calorie.getText().toString());
                bundle.putString("protein",et_protein.getText().toString());
                bundle.putString("date",dateData);
                bundle.putString("time",timeData);
                bundle.putByteArray("send_byteArray",send_byteArray);
                intent.putExtras(bundle);
                setResult(FOOD_RESULT_CODE,intent);
                finish();
            }
        });

        iv_addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //권한묻는곳
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED
                            && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                            && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==PackageManager.PERMISSION_GRANTED){
                        Log.d("222", "권한 설정 완료");
                    }
                    else {
                        Log.d("222", "권한 설정 요청");

                        ActivityCompat.requestPermissions(Frag_Activity_Addfood.this,new String[]
                                {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                }
                //권한끝냈고 이제 사진 찍기

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                activityLauncher_foodcamera.launch(cameraIntent);


            }
        });

    }
    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


}