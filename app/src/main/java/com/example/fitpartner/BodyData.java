package com.example.fitpartner;

import android.graphics.Bitmap;
import android.widget.TextView;

public class BodyData {

    private String tv_totalWeight;
    private String tv_fatRate;
    private String tv_proteinRate;
    private Bitmap iv_bodypicture;
    private String tv_picturedate;

    public BodyData(String tv_totalWeight, String tv_fatRate, String tv_proteinRate, Bitmap iv_bodypicture, String tv_picturedate) {
        this.tv_totalWeight = tv_totalWeight;
        this.tv_fatRate = tv_fatRate;
        this.tv_proteinRate = tv_proteinRate;
        this.iv_bodypicture = iv_bodypicture;
        this.tv_picturedate = tv_picturedate;
    }

    public String getTv_totalWeight() {
        return tv_totalWeight;
    }

    public void setTv_totalWeight(String tv_totalWeight) {
        this.tv_totalWeight = tv_totalWeight;
    }

    public String getTv_fatRate() {
        return tv_fatRate;
    }

    public void setTv_fatRate(String tv_fatRate) {
        this.tv_fatRate = tv_fatRate;
    }

    public String getTv_proteinRate() {
        return tv_proteinRate;
    }

    public void setTv_proteinRate(String tv_proteinRate) {
        this.tv_proteinRate = tv_proteinRate;
    }

    public Bitmap getIv_bodypicture() {
        return iv_bodypicture;
    }

    public void setIv_bodypicture(Bitmap iv_bodypicture) {
        this.iv_bodypicture = iv_bodypicture;
    }

    public String getTv_picturedate() {
        return tv_picturedate;
    }

    public void setTv_picturedate(String tv_picturedate) {
        this.tv_picturedate = tv_picturedate;
    }
}
