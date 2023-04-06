package com.example.foodineye_app;

import java.io.Serializable;

//MenuDetailActivity에서 Intent로 전달할 클래스 정의
public class Food implements Serializable {
    String s_id;
    String food_id;
    String menu_name;
    String menu_price;
    String menu_img_key;
    String menu_desc;
    String menu_allergy;
    String menu_origin;

    public Food(String s_id) {
        this.s_id = s_id;
    }
}
