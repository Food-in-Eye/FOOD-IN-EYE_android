package com.example.foodineye_app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderItem {

    @SerializedName("request")
    @Expose
    public String request;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("response")
    @Expose
    public OrderResponse orderResponse;

    public OrderResponse getOrderResponse() {
        return orderResponse;
    }

    public void setOrderResponse(OrderResponse orderResponse) {
        this.orderResponse = orderResponse;
    }

    public class OrderResponse {

        @SerializedName("_id")
        @Expose
        public String _id;

        @SerializedName("date")
        @Expose
        public String date;

        @SerializedName("u_id")
        @Expose
        public String u_id;

        @SerializedName("s_id")
        @Expose
        public String s_id;

        @SerializedName("m_id")
        @Expose
        public String m_id;

        @SerializedName("status")
        @Expose
        public int status;

        @SerializedName("f_list")
        @Expose
        public List<FoodList> f_list;

        // status == 0 -> 주문접수, status == 1 -> 조리중, status == 2 -> 조리완료
        public int isCook(){
            if(status == 2){
                return 2;
            }else if(status == 1){
                return 1;
            }else
                return 0;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getU_id() {
            return u_id;
        }

        public void setU_id(String u_id) {
            this.u_id = u_id;
        }

        public String getS_id() {
            return s_id;
        }

        public void setS_id(String s_id) {
            this.s_id = s_id;
        }

        public String getM_id() {
            return m_id;
        }

        public void setM_id(String m_id) {
            this.m_id = m_id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public List<FoodList> getF_list() {
            return f_list;
        }

        public void setF_list(List<FoodList> f_list) {
            this.f_list = f_list;
        }


        public class FoodList {

            @SerializedName("name")
            @Expose
            public String name;
            @SerializedName("count")
            @Expose
            public String count;
            @SerializedName("price")
            @Expose
            public String price;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCount() {
                return count;
            }

            public void setCount(String count) {
                this.count = count;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }
        }
    }
}
