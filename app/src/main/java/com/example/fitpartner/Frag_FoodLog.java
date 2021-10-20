package com.example.fitpartner;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
        foodAdapter = new FoodAdapter(foodArray, getActivity()); //아직 context는 안넘겨줌
        recyclerView.setAdapter(foodAdapter);
        // 설정완료

        //온클릭구현한거(어답터먼저) 메인에서 사용
        foodAdapter.setOnClickListener(new FoodAdapter.myRecyclerViewClickListener() {
            @Override
            public void whenItemClick(int position) {
                final FoodData item = foodArray.get(position);
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setIcon(R.mipmap.ic_launcher);
                ad.setTitle("수정하기");
                //다이얼로그 레이아웃이랑 연결해주는 부분임
                View dialog_view = inflater.inflate(R.layout.dialog_edit_food,null);
                EditText et_edcalorie = dialog_view.findViewById(R.id.editText_edcalorie);
                EditText et_edprotein = dialog_view.findViewById(R.id.editText_edprotein);

                et_edcalorie.setText(item.getTv_calorie());
                et_edprotein.setText(item.getTv_protein());
                //선택시 포커스가 끝으로가게
                Editable etext_cal = et_edcalorie.getText();
                Editable etext_pro = et_edprotein.getText();
                Selection.setSelection(etext_cal,etext_cal.length());
                Selection.setSelection(etext_pro,etext_pro.length());

                ad.setView(dialog_view);

                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String result_calorie = et_edcalorie.getText().toString();
                        String result_protein = et_edprotein.getText().toString();
                        item.setTv_calorie(result_calorie);
                        item.setTv_protein(result_protein);
                        foodAdapter.notifyItemChanged(position);
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
                et_edcalorie.requestFocus();
            }
        });


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
                        FoodData food;
                        if (result.getResultCode() == FOOD_RESULT_CODE){ //코드가 맞을경우
                            Intent intent = result.getData();
                            Bundle bundle = intent.getExtras();
                            if(bundle != null){ //번들에 값이 있으면
                                //int picture = bundle.getInt("사진데이터")
                                String calorie = bundle.getString("calorie");
                                String protein = bundle.getString("protein");
                                String mdate = bundle.getString("date");
                                String mtime = bundle.getString("time");
                                byte[] mbyteArray = bundle.getByteArray("send_byteArray");
                                if (mbyteArray != null){
                                    Bitmap mbitmap = byteArrayToBitmap(mbyteArray);
                                    food = new FoodData(mbitmap,calorie,protein,mdate,mtime);
                                } else{ //사진없을때
                                    food = new FoodData(null,calorie,protein,mdate,mtime);
                                }


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
    public Bitmap byteArrayToBitmap(byte[] byteArray){
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        return bitmap;
    }



}
