package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HistoryDetail {

    @SerializedName("request")
    @Expose
    public String request;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("response")
    @Expose
    public HistoryDetailResponse historyDetailResponse;

    public String toString() {
        return "request: " + request + ", status: " + status + ", response: " + historyDetailResponse;
    }

    public static class HistoryDetailResponse {
        @SerializedName("date")
        @Expose
        public String date;

        @SerializedName("orders")
        @Expose
        public List<OrderItem> orders;

        public String toString() {
            return "date: " + date + ", orders: " + orders;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public static class OrderItem {
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
}
