package com.example.foodineye_app.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetOrder {

    @SerializedName("order_list")
    @Expose
    public List<nOrder> orderLists;

    public class nOrder {

        @SerializedName("o_id")
        @Expose
        public String o_id;

        @SerializedName("s_id")
        @Expose
        public String s_id;

        @SerializedName("s_name")
        @Expose
        public String s_name;

        @SerializedName("m_id")
        @Expose
        public String m_id;

        @SerializedName("status")
        @Expose
        public int status; // status == 0 -> 접수, status == 1 -> 조리중, status == 2 -> 조리완료

        @SerializedName("f_list")
        @Expose
        public List<FoodList> f_list;

        public List<FoodList> getF_list() {
            return f_list;
        }

        public void setF_list(List<FoodList> f_list) {
            this.f_list = f_list;
        }


        public nOrder(String o_id, String s_id, String s_name, String m_id, int status, List<FoodList> f_list) {
            this.o_id = o_id;
            this.s_id = s_id;
            this.s_name = s_name;
            this.m_id = m_id;
            this.status = status;
            this.f_list = f_list;
        }

        public class FoodList {

            @SerializedName("f_id")
            @Expose
            public String f_id;

            @SerializedName("f_name")
            @Expose
            public String name;

            @SerializedName("price")
            @Expose
            public int price;

            @SerializedName("count")
            @Expose
            public int count;


        }
    }
}
