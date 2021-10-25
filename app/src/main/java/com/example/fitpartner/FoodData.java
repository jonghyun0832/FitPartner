package com.example.fitpartner;

import android.graphics.Bitmap;

public class FoodData {

    private Bitmap iv_food_picture;
    private String tv_calorie;
    private String tv_protein;
    private String tv_fooddate;
    private String tv_foodtime;
    private String bitmapToString;

    public FoodData(Bitmap iv_food_picture, String tv_calorie, String tv_protein, String tv_fooddate, String tv_foodtime, String bitmapToString) {
        this.iv_food_picture = iv_food_picture;
        this.tv_calorie = tv_calorie;
        this.tv_protein = tv_protein;
        this.tv_fooddate = tv_fooddate;
        this.tv_foodtime = tv_foodtime;
        this.bitmapToString = bitmapToString;
    }

    public Bitmap getIv_food_picture() {
        return iv_food_picture;
    }

    public void setIv_food_picture(Bitmap iv_food_picture) {
        this.iv_food_picture = iv_food_picture;
    }

    public String getTv_calorie() {
        return tv_calorie;
    }

    public void setTv_calorie(String tv_calorie) {
        this.tv_calorie = tv_calorie;
    }

    public String getTv_protein() {
        return tv_protein;
    }

    public void setTv_protein(String tv_protein) {
        this.tv_protein = tv_protein;
    }

    public String getTv_fooddate() {
        return tv_fooddate;
    }

    public void setTv_fooddate(String tv_fooddate) {
        this.tv_fooddate = tv_fooddate;
    }

    public String getTv_foodtime() {
        return tv_foodtime;
    }

    public void setTv_foodtime(String tv_foodtime) {
        this.tv_foodtime = tv_foodtime;
    }

    public String getBitmapToString() {
        return bitmapToString;
    }

    public void setBitmapToString(String bitmapToString) {
        this.bitmapToString = bitmapToString;
    }
}
