package com.example.fitpartner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.nio.BufferUnderflowException;

public class Frag_Activity_Addfood extends AppCompatActivity {

    private ImageView iv_addPicture;
    private EditText et_calorie;
    private EditText et_protein;
    private Button btn_fdCancel;
    private Button btn_fdAdd;
    private static final int FOOD_RESULT_CODE = 5050;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frag_addfood);

        iv_addPicture = (ImageView)findViewById(R.id.imageView_addpicture);
        btn_fdCancel = (Button)findViewById(R.id.button_fdcancel);
        btn_fdAdd = (Button)findViewById(R.id.button_fdadd);
        et_calorie = (EditText)findViewById(R.id.editText_calorie);
        et_protein = (EditText)findViewById(R.id.editText_protein);

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
                intent.putExtras(bundle);
                setResult(FOOD_RESULT_CODE,intent);
                finish();
            }
        });

        iv_addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //클릭하면 권한확인하고 사진찍고 가지고오기
            }
        });

    }
}