package com.example.fitpartner;

public class WorkoutData {

    private int iv_recycle_icon;
    private String tv_exData;
    private String tv_startTime;

    public WorkoutData(int iv_recycle_icon, String tv_exData, String tv_startTime) {
        this.iv_recycle_icon = iv_recycle_icon;
        this.tv_exData = tv_exData;
        this.tv_startTime = tv_startTime;
    }

    public int getIv_recycle_icon() {
        return iv_recycle_icon;
    }

    public void setIv_recycle_icon(int iv_recycle_icon) {
        this.iv_recycle_icon = iv_recycle_icon;
    }

    public String getTv_exData() {
        return tv_exData;
    }

    public void setTv_exData(String tv_exData) {
        this.tv_exData = tv_exData;
    }

    public String getTv_startTime() {
        return tv_startTime;
    }

    public void setTv_startTime(String tv_startTime) {
        this.tv_startTime = tv_startTime;
    }

}
