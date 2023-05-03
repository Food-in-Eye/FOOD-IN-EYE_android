package com.example.foodineye_app;

import java.util.List;

public class PostOrder {
    String u_id;
    int total_price;
    List<StoreOrder> content;

    public PostOrder(String u_id, int total_price, List<StoreOrder> content) {
        this.u_id = u_id;
        this.total_price = total_price;
        this.content = content;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public List<StoreOrder> getContent() {
        return content;
    }

    public void setContent(List<StoreOrder> content) {
        this.content = content;
    }

    //StoreOrder class
    public class StoreOrder {
        String s_id;
        String m_id;
        List<FoodCount> f_list;

        public StoreOrder(String s_id, String m_id, List<FoodCount> f_list) {
            this.s_id = s_id;
            this.m_id = m_id;
            this.f_list = f_list;
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

        public List<FoodCount> getF_list() {
            return f_list;
        }

        public void setF_list(List<FoodCount> f_list) {
            this.f_list = f_list;
        }

        //FoodCount class
        public class FoodCount{
            String f_id;
            int count;

            public String getF_id() {
                return f_id;
            }

            public void setF_id(String f_id) {
                this.f_id = f_id;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }
        }
    }
}
