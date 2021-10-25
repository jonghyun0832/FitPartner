package com.example.fitpartner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class BodyAdapter extends RecyclerView.Adapter<BodyAdapter.BodyViewHolder> {

    private final String Filename = "20211023";

    public interface myRecyclerViewClickListener{
        void whenItemClick(int position);
    }
    private BodyAdapter.myRecyclerViewClickListener myListener;

    public void setOnClickListener(BodyAdapter.myRecyclerViewClickListener listener){
        myListener = listener;
    }


    private ArrayList<BodyData> arraylist;
    private Context context;

    public BodyAdapter(ArrayList<BodyData> arraylist, Context context) {
        this.arraylist = arraylist;
        this.context = context;
    }

    public void setAdapter(ArrayList<BodyData> arrayList){
        this.arraylist = arrayList;
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


    public void remove(int position){
        try{
            // 리사이클러뷰 데이터 삭제시 그에 해당하는 데이터 삭제
            String path = arraylist.get(position).getBodyBitmapToString();
            if(path != null){
                String [] patharray = path.split("/");
                context.deleteFile(patharray[patharray.length-1]);
            }
            arraylist.remove(position);
            notifyItemRemoved(position);
        }catch (IndexOutOfBoundsException ex){
            ex.printStackTrace();
        }
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
                    ad.setTitle("선택한 사진을 삭제하시겠습니까?");
                    ad.setCancelable(false);
                    ad.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            remove(pos);
                            SharedPreferences sharedPreferences = context.getSharedPreferences(Filename, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            Gson gson = new GsonBuilder().create();
                            String json = gson.toJson(arraylist);
                            editor.putString("Bodylog",json);
                            editor.apply();
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
