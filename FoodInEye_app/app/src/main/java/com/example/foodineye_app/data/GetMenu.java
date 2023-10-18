package com.example.foodineye_app.data;

import com.example.foodineye_app.activity.Cart;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

//각 가게의 메뉴들 정보
public class GetMenu {
    @SerializedName("_id")
    @Expose
    public String _id;

    @SerializedName("s_id")
    @Expose
    public String s_id;

    @SerializedName("s_num")
    @Expose
    public int s_num;

    @SerializedName("s_name")
    @Expose
    public String s_name;

    @SerializedName("date")
    @Expose
    public String date;

    @SerializedName("f_list")
    @Expose
    public List<Menus> menusList;

    @Override
    public String toString() {
        return "GetMenu{" +
                "_id='" + _id + '\'' +
                ", s_id='" + s_id + '\'' +
                ", date='" + date + '\'' +
                ", menus=" + menusList +
                ", s_num=" + s_num +
                '}';
    }

    //getter and setter
    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public List<Menus> getMenus() {
        return menusList;
    }

    public Cart findMenu(String s_id, String s_name, int s_num, String m_id, int fNum){
        for(Menus menus : menusList){
            if(menus.f_num == fNum){
                Cart cartCand = new Cart(s_id, s_name, m_id, menus.f_id, menus.name, menus.price, menus.img_key, s_num,fNum);
                return cartCand;
            }
        }
        return null;
    }

    public class Menus{

        @SerializedName("f_id")
        @Expose
        private  String f_id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("price")
        @Expose
        private int price;
        @SerializedName("img_key")
        @Expose
        private String img_key;
        @SerializedName("desc")
        @Expose
        private String desc;
        @SerializedName("allergy")
        @Expose
        private String allergy;
        @SerializedName("origin")
        @Expose
        private String origin;
        @SerializedName("num")
        @Expose
        private int f_num;

        @Override
        public String toString() {
            return "Menus{" +
                    "f_id='" + f_id + '\'' +
                    ", name='" + name + '\'' +
                    ", price=" + price +
                    ", img_key='" + img_key + '\'' +
                    ", desc='" + desc + '\'' +
                    ", allergy='" + allergy + '\'' +
                    ", origin='" + origin + '\'' +
                    ", f_num=" + f_num +
                    '}';
        }

        //getter and setter
        public String getName() {  return name;   }

        public void setName(String name) {
            this.name = name;
        }

        public String getF_id() {
            return f_id;
        }

        public int getPrice() {
            return price;
        }

        public String getImg_key() {
            return img_key;
        }

        public String getDesc() {
            return desc;
        }

        public String getAllergy() {
            return allergy;
        }

        public String getOrigin() {
            return origin;
        }

        public int getNum() {
            return f_num;
        }
    }
}
