package com.example.foodineye_app.data;

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
    public String s_num;

    @SerializedName("date")
    @Expose
    public String date;

    @SerializedName("f_list")
    @Expose
    public List<Menus> menus;

    @Override
    public String toString() {
        return "GetMenu{" +
                "_id='" + _id + '\'' +
                ", s_id='" + s_id + '\'' +
                ", date='" + date + '\'' +
                ", menus=" + menus +
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
        return menus;
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
