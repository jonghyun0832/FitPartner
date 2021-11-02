package com.example.fitpartner;

import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StopWatchAdapter extends RecyclerView.Adapter<StopWatchAdapter.StopWatchViewHolder> {

    ArrayList<StopWatchData> arrayList;

    public StopWatchAdapter(ArrayList<StopWatchData> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public StopWatchAdapter.StopWatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_stopwatch_item,parent,false);
        StopWatchViewHolder holder = new StopWatchViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StopWatchAdapter.StopWatchViewHolder holder, int position) {
        holder.tv_time.setText(arrayList.get(position).getTime());
        holder.tv_num.setText(position + "");

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class StopWatchViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_time;
        public TextView tv_num;


        public StopWatchViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_time = itemView.findViewById(R.id.textView_recycle_record);
            tv_num = itemView.findViewById(R.id.textView_recycle_num);

        }
    }
}
