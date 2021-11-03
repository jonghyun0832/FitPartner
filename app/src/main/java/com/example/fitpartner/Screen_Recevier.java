package com.example.fitpartner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class Screen_Recevier extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){ //intent.getAction().equals(Intent.ACTION_SCREEN_OFF) ||
            Log.d("111", "onReceive: 스크린리시버");
            intent = new Intent(context,Activity_Front_Screen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);

        } else {
            Log.d("111", "onReceive: 스크린OFF");
        }


    }
}