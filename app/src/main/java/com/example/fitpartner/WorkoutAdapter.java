package com.example.fitpartner;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {


    //클릭이벤트 리스너 인터페이스
    public interface OnItemClickEventListener {
        void onItemClick(View a_view, int a_position);
    }

    private OnItemClickEventListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickEventListener a_listener){
        mItemClickListener = a_listener;
    }

    private ArrayList<WorkoutData> arrayList;
    private Context context;

    public WorkoutAdapter(ArrayList<WorkoutData> arrayList, Context context) { //콜백
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public WorkoutAdapter.WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Log.d("cccheck", "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_workoutlog_item,parent,false);
        WorkoutViewHolder holder =new WorkoutViewHolder(view,mItemClickListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutAdapter.WorkoutViewHolder holder, int position) {
        //Log.d("cccheck", "onBindViewHolder: " + position);
        holder.iv_recycle_icon.setImageResource(arrayList.get(position).getIv_recycle_icon());
        holder.tv_exData.setText(arrayList.get(position).getTv_exData());
        //추가필요
        holder.tv_starttime.setText(arrayList.get(position).getTv_startTime());
        holder.tv_endtime.setText(arrayList.get(position).getTv_endTime());
        holder.tv_date.setText(arrayList.get(position).getTv_date());


    }

    @Override
    public int getItemCount() {
        return (null!=arrayList ? arrayList.size() : 0);
    }

    //삭제구현
    public void remove(int position){
        try{
            arrayList.remove(position);
            notifyItemRemoved(position);
        }catch (IndexOutOfBoundsException ex){
            ex.printStackTrace();
        }
    }


    public class WorkoutViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_recycle_icon;
        public TextView tv_exData;
        //추가필요
        public TextView tv_starttime;
        public TextView tv_endtime;
        public TextView tv_date;


        public WorkoutViewHolder(@NonNull View itemView, final OnItemClickEventListener mItemClickListener) {
            super(itemView);
            this.iv_recycle_icon = (ImageView) itemView.findViewById(R.id.imageView_recycle_icon);
            this.tv_exData = (TextView)itemView.findViewById(R.id.recycle_exData);
            //추가필요
            this.tv_starttime = (TextView)itemView.findViewById(R.id.recycle_starttime);
            this.tv_endtime = (TextView)itemView.findViewById(R.id.recycle_endtime);
            this.tv_date = (TextView)itemView.findViewById(R.id.textView_date);

            //클릭시
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int pos = getBindingAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        mItemClickListener.onItemClick(view,pos);
                    }
                }
            });

            //롱클릭시
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final int pos = getBindingAdapterPosition();
                    androidx.appcompat.app.AlertDialog.Builder ad = new AlertDialog.Builder(context);
                    ad.setIcon(R.mipmap.ic_launcher);
                    ad.setTitle("삭제하시겠습니까?");
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


            //클릭될때
            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int pos = getBindingAdapterPosition();
                    if(listener != null){
                        listener.OnItemClick(WorkoutViewHolder.this,v,pos);
                    }
                    *//*if(pos != RecyclerView.NO_POSITION){

                    }*//*
                }
            });*/

        }
    }

}
