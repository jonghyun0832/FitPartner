package com.example.fitpartner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URI;


public class SubActivity_Mypage extends AppCompatActivity {

    /*private Button btn_camera;
    private ImageView img_myimage;
    private static final int REQUEST_IMAGE_CODE = 101;*/
    private static final String TAG = "SubActivity_Mypage";

    private TextView txt_name;
    private TextView txt_weight;
    private TextView txt_gender;
    private Button btn_edit;
    private Button btn_camera;
    private Button btn_gallery;
    private ImageView img_mypicture;
    private SharedPreferences preferences;
    private final static int TAKE_PICTURE = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_mypage);
        Log.d(TAG, "onCreatemypage: ");

        //주소 이어주기
        txt_name = (TextView)findViewById(R.id.textView_getname);
        txt_weight = (TextView)findViewById(R.id.textView_getweight);
        txt_gender = (TextView)findViewById(R.id.textView_getgender);
        btn_edit = (Button)findViewById(R.id.button_edit);
        btn_camera = (Button)findViewById(R.id.button_camera);
        btn_gallery = (Button)findViewById(R.id.button_gallery);
        img_mypicture = (ImageView)findViewById(R.id.imageView_mypicture);

        //내부저장소 선언
        preferences = getSharedPreferences("MypageInfo", MODE_PRIVATE); //앱내부 저장소에 데이터 저장하기

        //수정하기 버튼 클릭시
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //명시적 인텐트
                Intent intent = new Intent(SubActivity_Mypage.this,SubActivity_Edit.class);

                //sharedpreference에 데이터 저장시키기
                SharedPreferences.Editor mypageEditor = preferences.edit();
                mypageEditor.putString("myname", txt_name.getText().toString());
                mypageEditor.putString("myweight",txt_weight.getText().toString());
                mypageEditor.commit();

                activityLauncher.launch(intent);
            }
        });

        // 권한 묻는곳임
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "권한 설정 완료");
            }
            else {
                Log.d(TAG, "권한 설정 요청");

                ActivityCompat.requestPermissions(SubActivity_Mypage.this,new String[]
                        {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }

        //카메라 버튼 눌렀을떄
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.button_camera:
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        activityLauncher_camera.launch(cameraIntent);
                        break;
                }
            }
        });
        //갤러리 버튼 눌렀을때
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK); //MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                galleryIntent.setType("image/*");
                activityLauncher_gallery.launch(galleryIntent);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        Log.d(TAG, "onRequestPermissionsResult: ");
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "PERMISSION: " + permissions[0] + "was" + grantResults[0]);
        }
    }

    //인텐트 번들 받아오기
    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");

                    if (result.getResultCode() == 78){ //코드가 맞을경우
                        Intent intent = result.getData();
                        Bundle bundle = intent.getExtras();

                        if(bundle != null){ //번들에 값이 있으면
                            String name = bundle.getString("name");
                            String weight = bundle.getString("weight");
                            String gender = bundle.getString("gender");
                            txt_name.setText(name);
                            txt_weight.setText(weight);
                            txt_gender.setText(gender);
                        }
                    }
                }
            }
    );

    //카메라 이미지 받아오기
    ActivityResultLauncher<Intent> activityLauncher_camera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult:LAUNCH ");
                    if (result.getResultCode() == RESULT_OK){ //코드가 맞을경우
                        Log.d(TAG, "onActivityResult:RESULT OK ");
                        Intent cameraIntent = result.getData();
                        Bitmap bitmap = (Bitmap) cameraIntent.getExtras().get("data");
                        if(bitmap != null){
                            Log.d(TAG, "onActivityResult:비트맵안비었음 ");
                            img_mypicture.setImageBitmap(bitmap);
                        }
                    }
                }
            }
    );


    ActivityResultLauncher<Intent> activityLauncher_gallery = registerForActivityResult( //갤러리에서 이미지 받아오기
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "gallery_pick ");
                    Intent galleryIntent = result.getData();
                    if(galleryIntent != null){
                        try {
                            //resolver, uri 넣어서 비트맵으로
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(),galleryIntent.getData()
                            );
                            img_mypicture.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );


}