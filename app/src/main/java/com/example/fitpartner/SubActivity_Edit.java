package com.example.fitpartner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

public class SubActivity_Edit extends AppCompatActivity {

    private EditText et_name;
    private EditText et_weight;
    private Button btn_send;
    private Button btn_cancel;
    private SharedPreferences preferences;
    //private static final String TAG = "SubActivity_Mypage777";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_edit);


        Spinner genderSpinner = (Spinner)findViewById(R.id.spinner_gender);
        ArrayAdapter genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);


        et_name = (EditText)findViewById(R.id.editText_setname);
        et_weight = (EditText)findViewById(R.id.editText_setweight);
        btn_send = (Button)findViewById(R.id.button_send);
        btn_cancel = (Button)findViewById(R.id.button_cancel);

        preferences = getSharedPreferences("MypageInfo",MODE_PRIVATE);

        et_name.setText(preferences.getString("myname",""));
        et_weight.setText(preferences.getString("myweight",""));


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("name",et_name.getText().toString());
                bundle.putString("weight",et_weight.getText().toString());
                bundle.putString("gender", genderSpinner.getSelectedItem().toString());
                intent.putExtras(bundle);

                setResult(78,intent);

                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage();

            }
        });

    }


    public void showMessage(){
        Log.d("dsdsdsdasdwqdwsadsadw", "showMessage: ");
        AlertDialog.Builder ad = new AlertDialog.Builder(SubActivity_Edit.this);
        ad.setIcon(R.mipmap.ic_launcher);
        ad.setTitle("돌아가시겠습니까?");
        ad.setMessage("돌아가면 변경된 정보는 저장되지 않습니다.");
        ad.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        ad.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = ad.create();
        alertDialog.show();
    }
}