package com.example.fitpartner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>{


    private ArrayList<WorkoutData> arrayList;

    public WorkoutAdapter(ArrayList<WorkoutData> arrayList) {
        this.arrayList = arrayList;
    }


    @NonNull
    @Override
    public WorkoutAdapter.WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_workoutlog_item,parent,false);
        WorkoutViewHolder holder =new WorkoutViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutAdapter.WorkoutViewHolder holder, int position) {
        holder.iv_recycle_icon.setImageResource(arrayList.get(position).getIv_recycle_icon());
        holder.tv_exData.setText(arrayList.get(position).getTv_exData());

    }

    @Override
    public int getItemCount() {
        return (null!=arrayList ? arrayList.size() : 0);
        //return 0;
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_recycle_icon;
        public TextView tv_exData;


        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_recycle_icon = (ImageView) itemView.findViewById(R.id.imageView_recycle_icon);
            this.tv_exData = (TextView)itemView.findViewById(R.id.recycle_exData);
        }
    }
}
