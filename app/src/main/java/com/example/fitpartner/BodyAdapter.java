package com.example.fitpartner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class BodyAdapter extends RecyclerView.Adapter<BodyAdapter.BodyViewHolder> {

    private ArrayList<BodyData> arraylist;

    public BodyAdapter(ArrayList<BodyData> arraylist) {
        this.arraylist = arraylist;
    }

    @NonNull
    @Override
    public BodyAdapter.BodyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_pciturelog_item,parent,false);
        BodyAdapter.BodyViewHolder holder = new BodyAdapter.BodyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BodyAdapter.BodyViewHolder holder, int position) {
        holder.iv_bodyPicture.setImageBitmap(arraylist.get(position).getIv_bodypicture());
        holder.tv_totalWeight.setText(arraylist.get(position).getTv_totalWeight());
        holder.tv_fatRate.setText(arraylist.get(position).getTv_fatRate());
        holder.tv_proteinRate.setText(arraylist.get(position).getTv_proteinRate());
        holder.tv_pictureDate.setText(arraylist.get(position).getTv_picturedate());

    }



    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public class BodyViewHolder extends RecyclerView.ViewHolder{
        public ImageView iv_bodyPicture;
        public TextView tv_totalWeight;
        public TextView tv_fatRate;
        public TextView tv_proteinRate;
        public TextView tv_pictureDate;

        public BodyViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_bodyPicture = itemView.findViewById(R.id.imageView_bodypicture);
            tv_totalWeight = itemView.findViewById(R.id.textView_totalWeight);
            tv_fatRate = itemView.findViewById(R.id.textView_fatRate);
            tv_proteinRate = itemView.findViewById(R.id.textView_proteinRate);
            tv_pictureDate = itemView.findViewById(R.id.textView_pictureDate);


        }
    }
}
