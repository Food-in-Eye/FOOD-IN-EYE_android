package com.example.foodineye_app.activity;

import java.io.Serializable;


public class Food implements Serializable {

    String food_id;
    String m_name;
    int m_price;
    String m_img_key;
    String m_desc;
    String m_allergy;
    String m_origin;
    int f_num;

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public String getM_name() {
        return m_name;
    }

    public void setM_name(String m_name) {
        this.m_name = m_name;
    }

    public int getM_price() {
        return m_price;
    }

    public void setM_price(int m_price) {
        this.m_price = m_price;
    }

    public String getM_img_key() {
        return m_img_key;
    }

    public void setM_img_key(String m_img_key) {
        this.m_img_key = m_img_key;
    }

    public String getM_desc() {
        return m_desc;
    }

    public void setM_desc(String m_desc) {
        this.m_desc = m_desc;
    }

    public String getM_allergy() {
        return m_allergy;
    }

    public void setM_allergy(String m_allergy) {
        this.m_allergy = m_allergy;
    }

    public String getM_origin() {
        return m_origin;
    }

    public void setM_origin(String m_origin) {
        this.m_origin = m_origin;
    }

    public int getF_num() {
        return f_num;
    }

    public void setF_num(int f_num) {
        this.f_num = f_num;
    }
}
