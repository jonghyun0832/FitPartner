package com.example.fitpartner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Frag_FoodLog extends Fragment {

    private View view;
    private TextView tv_water;
    private ImageButton imgbtn_plus;
    private ImageButton imgbtn_minus;
    private ImageView iv_addfood;
    private int waters;

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

        //리사이클러뷰 만들기
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_foodlog);
        gridLayoutManager = new GridLayoutManager(getActivity(),2);

        /*gridLayoutManager.setReverseLayout(true);
        gridLayoutManager.setStackFromEnd(true);*/

        recyclerView.setLayoutManager(gridLayoutManager);

        foodArray = new ArrayList<>();
        foodAdapter = new FoodAdapter(foodArray); //아직 context는 안넘겨줌
        recyclerView.setAdapter(foodAdapter);
        // 설정완료


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


        ActivityResultLauncher<Intent> activityLauncher_fdadd = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == FOOD_RESULT_CODE){ //코드가 맞을경우
                            Intent intent = result.getData();
                            Bundle bundle = intent.getExtras();
                            if(bundle != null){ //번들에 값이 있으면
                                //int picture = bundle.getInt("사진데이터")
                                String calorie = bundle.getString("calorie");
                                String protein = bundle.getString("protein");
                                Log.d("222", "onActivityResult:받음 ");
                                FoodData food = new FoodData(R.drawable.ic_excellent,calorie,protein);
                                foodArray.add(food);
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



}
