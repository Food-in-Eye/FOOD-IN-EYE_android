package com.example.foodineye_app.activity;

import java.io.Serializable;

//MenuDetailActivity에서 Intent로 전달할 클래스 정의
public class IntentToDetail implements Serializable {

    String s_id;
    String m_id;
    String s_name;

    Food food = new Food();
    int s_num;

    public IntentToDetail(String s_id, String m_id, String s_name, Food food, int s_num) {
        this.s_id = s_id;
        this.m_id = m_id;
        this.s_name = s_name;
        this.food = food;
        this.s_num = s_num;
    }

    public String getS_name() {  return s_name;  }

    public void setS_name(String s_name) {   this.s_name = s_name;  }

    public String getM_id() {  return m_id;  }

    public void setM_id(String m_id) {   this.m_id = m_id;   }

    public String getS_id() {   return s_id;  }

    public void setS_id(String s_id) {   this.s_id = s_id;   }

    public Food getFood() {   return food;  }

    public void setFood(Food food) {    this.food = food;   }

    public int getS_num() {
        return s_num;
    }

    public void setS_num(int s_num) {
        this.s_num = s_num;
    }

    public String toString(){
        return "s_id: "+s_id+ " m_id: "+m_id+ " s_name: "+s_name;
    }
}
