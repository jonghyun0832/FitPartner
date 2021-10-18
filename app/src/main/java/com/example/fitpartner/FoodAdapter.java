package com.example.fitpartner;

import android.provider.ContactsContract;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private ArrayList<FoodData> arraylist;


    public FoodAdapter(ArrayList<FoodData> arraylist) {
        this.arraylist = arraylist;
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
        holder.iv_food_picture.setImageResource(arraylist.get(position).getIv_food_picture());
        holder.tv_calorie.setText(arraylist.get(position).getTv_calorie());
        holder.tv_protein.setText(arraylist.get(position).getTv_protein());
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_food_picture;
        public TextView tv_calorie;
        public TextView tv_protein;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_food_picture = (ImageView)itemView.findViewById(R.id.imageView_foodimg);
            this.tv_calorie = (TextView)itemView.findViewById(R.id.textView_calorie);
            this.tv_protein = (TextView)itemView.findViewById(R.id.textView_protein);


        }
    }
}
