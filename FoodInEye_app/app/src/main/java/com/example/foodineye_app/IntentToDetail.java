package com.example.foodineye_app;

import java.io.Serializable;

//MenuDetailActivity에서 Intent로 전달할 클래스 정의
public class IntentToDetail implements Serializable {

    String s_id;
    String m_id;

    Food food = new Food();

    public IntentToDetail(String s_id, String m_id, Food food) {
        this.s_id = s_id;
        this.m_id = m_id;
        this.food = food;
    }

    public String getM_id() {  return m_id;  }

    public void setM_id(String m_id) {   this.m_id = m_id;   }

    public String getS_id() {   return s_id;  }

    public void setS_id(String s_id) {   this.s_id = s_id;   }

    public Food getFood() {   return food;  }

    public void setFood(Food food) {    this.food = food;   }

    public String toString(){
        return "s_id: "+s_id+ " m_id: "+m_id;
    };
}
