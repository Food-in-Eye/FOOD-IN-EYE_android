package com.example.foodineye_app.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class History {

    @SerializedName("max_batch")
    @Expose
    public int max_batch;

    @SerializedName("history_list")
    @Expose
    public List<HistoryResponse> historyResponse;

    @Override
    public String toString() {
        return "History{" +
                "max_batch=" + max_batch +
                ", historyResponse=" + historyResponse +
                '}';
    }

    public int getMax_batch() {
        return max_batch;
    }

    public class HistoryResponse {

        @SerializedName("h_id")
        @Expose
        public String h_id;

        @SerializedName("date")
        @Expose
        public String date;

        @SerializedName("total_price")
        @Expose
        public int total_price;

        @SerializedName("s_names")
        @Expose
        public List<String> s_names;

        public String toString() {
            return "h_id: " + h_id + ", date: " + date + ", total_price: " + total_price + ", s_names: " + s_names;
        }

        public String getH_id() {
            return h_id;
        }

        public void setH_id(String h_id) {
            this.h_id = h_id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getTotal_price() {
            return total_price;
        }

        public void setTotal_price(int total_price) {
            this.total_price = total_price;
        }

        public List<String> getS_names() {
            return s_names;
        }

        public void setS_names(List<String> s_names) {
            this.s_names = s_names;
        }
    }


}
