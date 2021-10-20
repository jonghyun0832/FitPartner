package com.example.fitpartner;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    //리사이클러뷰에는 온클릭리스너가 없으니까 인터페이스로 구현해보자
    public interface myRecyclerViewClickListener{
        //내가누른 아이템의 포지션을 외부에서 알수있게 전달하겠다
        void whenItemClick(int position);
    }
    //이 인터페이스를 내부에서 하나 들고있어야한다.
    private myRecyclerViewClickListener myListener;

    //외부에서 리스너를 지정할 수 있도록 메소드 준비 - 외부에서 리스너 받아와서 myListener에 넣어줌
    public void setOnClickListener(myRecyclerViewClickListener listener){
        myListener = listener;
    }
    //실제로 클릭이 일어나는 부분은 bindviewholder에서 처리함

    private ArrayList<FoodData> arraylist;
    private Context context;

    public FoodAdapter(ArrayList<FoodData> arraylist, Context context) {
        this.arraylist = arraylist;
        this.context = context;
    }


    @NonNull
    @Override
    public FoodAdapter.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_foodlog_item,parent,false);
        FoodViewHolder holder = new FoodViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.FoodViewHolder holder, int position) {
        holder.iv_food_picture.setImageBitmap(arraylist.get(position).getIv_food_picture());
        holder.tv_calorie.setText(arraylist.get(position).getTv_calorie());
        holder.tv_protein.setText(arraylist.get(position).getTv_protein());
        holder.tv_fooddate.setText(arraylist.get(position).getTv_fooddate());
        holder.tv_foodtime.setText(arraylist.get(position).getTv_foodtime());


    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    //삭제
    public void remove(int position){
        try{
            arraylist.remove(position);
            notifyItemRemoved(position);
        }catch (IndexOutOfBoundsException ex){
            ex.printStackTrace();
        }
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_food_picture;
        public TextView tv_calorie;
        public TextView tv_protein;
        public TextView tv_fooddate;
        public TextView tv_foodtime;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_food_picture = (ImageView)itemView.findViewById(R.id.imageView_foodimg);
            this.tv_calorie = (TextView)itemView.findViewById(R.id.textView_calorie);
            this.tv_protein = (TextView)itemView.findViewById(R.id.textView_protein);
            this.tv_fooddate = (TextView)itemView.findViewById(R.id.textView_fooddate);
            this.tv_foodtime = (TextView)itemView.findViewById(R.id.textView_foodtime);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int pos = getBindingAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        myListener.whenItemClick(pos);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final int pos = getBindingAdapterPosition();
                    AlertDialog.Builder ad = new AlertDialog.Builder(context);
                    ad.setIcon(R.mipmap.ic_launcher);
                    ad.setTitle("식사 기록을 삭제하시겠습니까?");
                    ad.setCancelable(false);
                    ad.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            remove(pos);
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


                    return true;
                }
            });


        }
    }
}
