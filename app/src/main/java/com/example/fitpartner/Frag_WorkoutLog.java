package com.example.fitpartner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Frag_WorkoutLog extends Fragment {

    private Button btn_add;
    private Button btn_prac;
    private ArrayList<WorkoutData> workoutarray;
    private WorkoutAdapter workoutAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_workoutlog,container,false);

        btn_add = (Button)view.findViewById(R.id.button_add);
        btn_prac = (Button)view.findViewById(R.id.button_prac);
        //리사이클러뷰용
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_workout);
        linearLayoutManager = new LinearLayoutManager(getActivity()); //컨택스트 넣는곳
        //리사이클러뷰에 레이아웃 매니저 장착
        recyclerView.setLayoutManager(linearLayoutManager);

        workoutarray = new ArrayList<>();

        //리사이클러뷰에 어댑터 장착
        workoutAdapter = new WorkoutAdapter(workoutarray);
        recyclerView.setAdapter(workoutAdapter);

        btn_prac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("prac", "onClick: ");
                WorkoutData work1 = new WorkoutData(R.drawable.ic_launcher_background,"팔굽100개 윗몸100개");
                workoutarray.add(work1);
                workoutAdapter.notifyDataSetChanged();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(getActivity(),Frag_Activity_Additem.class); //getcontext
                startActivity(addIntent);
            }
        });

        return view;

    }

}
