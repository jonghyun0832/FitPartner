package com.example.fitpartner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Frag_FoodLog extends Fragment {

    private View view;
    private TextView tv_water;
    private ImageButton imgbtn_plus;
    private ImageButton imgbtn_minus;
    private int waters;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //초기화해야하는 리소스들 , 프래그먼트 생성하면서 넘겨준값들이 있으면 여기서 변수 넣어주기

    }

    //onactivitycreated 는 없어졌고 LifecycleObserver를 써야된다.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_foodlog,container,false);

        tv_water = view.findViewById(R.id.textView_water);
        imgbtn_plus = view.findViewById(R.id.imageButton_plus);
        imgbtn_minus = view.findViewById(R.id.imageButton_minus);


        //+버튼 눌렀을때
        imgbtn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waters += 100;
                tv_water.setText(waters + " mL");
            }
        });

        // -버튼 눌렀을때
        imgbtn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waters -= 100;
                tv_water.setText(waters + " mL");
            }
        });

        tv_water.setText(waters + " mL");

        return view;
    }



}
