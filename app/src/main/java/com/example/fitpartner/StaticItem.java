package com.example.fitpartner;

import android.app.Application;

public class StaticItem extends Application {

    private String date;

    @Override
    public void onCreate() {
        super.onCreate();
        date = "";
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
