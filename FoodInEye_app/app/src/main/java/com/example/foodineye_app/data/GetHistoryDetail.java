package com.example.foodineye_app.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetHistoryDetail {
    @SerializedName("date")
    @Expose
    public String date;

    @SerializedName("orders")
    @Expose
    public List<GetHistoryDetail.OrderItem> orders;

    public String toString() {
        return "date: " + date + ", orders: " + orders;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<GetHistoryDetail.OrderItem> getOrders() {
        return orders;
    }

    public class OrderItem {
        @SerializedName("s_name")
        @Expose
        public String s_name;

        @SerializedName("f_name")
        @Expose
        public String f_name;

        @SerializedName("count")
        @Expose
        public int count;

        @SerializedName("price")
        @Expose
        public int price;

        public String toString() {
            return "s_name: " + s_name + ", f_name: " + f_name + ", count: " + count + ", price: " + price;
        }

        public String getS_name() {
            return s_name;
        }

        public void setS_name(String s_name) {
            this.s_name = s_name;
        }

        public String getF_name() {
            return f_name;
        }

        public void setF_name(String f_name) {
            this.f_name = f_name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

    }
}