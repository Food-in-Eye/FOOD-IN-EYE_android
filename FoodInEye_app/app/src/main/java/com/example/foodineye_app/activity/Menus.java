package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Menus {

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
    private int num;


    @Override
    public String toString()
    {
        return "Menu [img_key = "+", price = "+price+", origin = "+origin+", name = "+name+", allergy = "+allergy+", desc = "+desc+"]";
    }

    //getter and setter

    public String getf_id() {  return f_id;  }

    public void setf_id(String _id) {  this.f_id = f_id;  }

    public String getName() {  return name;   }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImg_key() {
        return img_key;
    }

    public void setImg_key(String img_key) {
        this.img_key = img_key;
    }

    public String getM_desc() {
        return desc;
    }

    public void setM_desc(String desc) {
        this.desc = desc;
    }

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
