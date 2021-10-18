package com.example.fitpartner;

public class FoodData {

    private int iv_food_picture;
    private String tv_calorie;
    private String tv_protein;

    public FoodData(int iv_food_picture, String tv_calorie, String tv_protein) {
        this.iv_food_picture = iv_food_picture;
        this.tv_calorie = tv_calorie;
        this.tv_protein = tv_protein;
    }

    public int getIv_food_picture() {
        return iv_food_picture;
    }

    public void setIv_food_picture(int iv_food_picture) {
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
}
