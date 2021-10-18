package com.example.fitpartner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Frag_WorkoutLog extends Fragment { //implements View.OnClickListener

    private Button btn_add;
    //private Button btn_prac;
    private ArrayList<WorkoutData> workoutarray;
    private WorkoutAdapter workoutAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;


    private static final String TAG = "WorkoutLog";
    private static final int WOKROUT_RESULT_CODE = 7070;
    private static final int EDIT_CODE = 8080;

    private int img_path;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("111", "onAttach: ");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("111", "onCreateView: workout");
        View view = inflater.inflate(R.layout.frag_workoutlog,container,false);


        btn_add = (Button)view.findViewById(R.id.button_add);

        //btn_add.setOnClickListener(this); -> implement 해서 new가 문제인지 확인해보기

        //btn_prac = (Button)view.findViewById(R.id.button_prac);
        //리사이클러뷰용
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_workout);
        linearLayoutManager = new LinearLayoutManager(getActivity());//컨택스트 넣는곳

        //거꾸로 설정하는곳
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        //리사이클러뷰에 레이아웃 매니저 장착
        recyclerView.setLayoutManager(linearLayoutManager);

        workoutarray = new ArrayList<>();

        //리사이클러뷰에 어댑터 장착
        workoutAdapter = new WorkoutAdapter(workoutarray,getActivity());
        recyclerView.setAdapter(workoutAdapter);


        /*btn_prac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("prac", "onClick: ");
                WorkoutData work1 = new WorkoutData(R.drawable.circlebutton,"팔굽100개 윗몸100개","ddsdsd");
                workoutarray.add(work1);
                workoutAdapter.notifyDataSetChanged();
            }
        });*/


        ActivityResultLauncher<Intent> activityLauncher_edit = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == EDIT_CODE){ //코드가 맞을경우
                            Intent intent = result.getData();
                            Bundle edit_bundle = intent.getExtras();
                            if(edit_bundle != null){ //번들에 값이 있으면
                                Log.d("111", "onActivityResult: 잘받아왔네요");
                                String exdata = edit_bundle.getString("exdata");
                                String starttime = edit_bundle.getString("starttime");
                                String endtime = edit_bundle.getString("endtime");
                                int concentrate = edit_bundle.getInt("concentrate");
                                int a_position = edit_bundle.getInt("return_position");
                                final WorkoutData item = workoutarray.get(a_position);
                                if (concentrate == 0){
                                    img_path = R.drawable.ic_excellent;
                                } else if (concentrate == 1){
                                    img_path = R.drawable.ic_good;
                                } else if (concentrate == 2){
                                    img_path = R.drawable.ic_normal;
                                } else if (concentrate == 3){
                                    img_path = R.drawable.ic_bad;
                                } else {
                                    img_path = R.drawable.ic_terrible;
                                }
                                item.setIv_recycle_icon(img_path);
                                item.setTv_exData(exdata);
                                item.setTv_startTime(starttime);
                                item.setTv_endTime(endtime);
                                workoutAdapter.notifyItemChanged(a_position);
                            }
                        }
                    }
                }
        );

        workoutAdapter.setOnItemClickListener(new WorkoutAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(View a_view, int a_position) {
                final WorkoutData item = workoutarray.get(a_position);
                Intent editIntent = new Intent(getActivity(),Frag_Activity_Additem.class);
                Bundle editbundle = new Bundle();
                editbundle.putString("edit_exdata",item.getTv_exData());
                editbundle.putString("edit_starttime",item.getTv_startTime());
                editbundle.putString("edit_endtime",item.getTv_endTime());
                editbundle.putInt("a_position",a_position);
                editbundle.putInt("spinner",item.getIv_recycle_icon());
                editIntent.putExtras(editbundle);
                activityLauncher_edit.launch(editIntent);
                //startActivity(editIntent);

                //Toast.makeText(getActivity(), item.getTv_startTime(), Toast.LENGTH_SHORT).show();
            }
        });



        ActivityResultLauncher<Intent> activityLauncher_add = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.d("111", "onActivityResult: 작동부분");

                        if (result.getResultCode() == WOKROUT_RESULT_CODE){ //코드가 맞을경우
                            Intent intent = result.getData();
                            Bundle bundle = intent.getExtras();
                            if(bundle != null){ //번들에 값이 있으면
                                Log.d("111", "onActivityResult: 받아서데이터풀기");
                                String exdata = bundle.getString("exdata");
                                String starttime = bundle.getString("starttime");
                                String endtime = bundle.getString("endtime");
                                String todaydate = bundle.getString("date");
                                int concentrate = bundle.getInt("concentrate");
                                if (concentrate == 0){
                                    img_path = R.drawable.ic_excellent;
                                } else if (concentrate == 1){
                                    img_path = R.drawable.ic_good;
                                } else if (concentrate == 2){
                                    img_path = R.drawable.ic_normal;
                                } else if (concentrate == 3){
                                    img_path = R.drawable.ic_bad;
                                } else {
                                    img_path = R.drawable.ic_terrible;
                                }
                                WorkoutData workout = new WorkoutData(img_path,exdata,starttime,endtime,todaydate);
                                workoutarray.add(workout);
                                workoutAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
        );

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("111", "onClick:btn_add ");
                Intent addIntent = new Intent(getActivity(),Frag_Activity_Additem.class); //getcontext
                Log.d("111", "onClick: 런처작동");
                //startActivity(addIntent);
                activityLauncher_add.launch(addIntent);
            }
        });
        return view;
    }






    //new 가 문제인지 확인해보기
    /*@Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_add){
            Intent addIntent = new Intent(getActivity(),Frag_Activity_Additem.class); //getcontext
            activityLauncher_add.launch(addIntent);
        }
    }*/
}
