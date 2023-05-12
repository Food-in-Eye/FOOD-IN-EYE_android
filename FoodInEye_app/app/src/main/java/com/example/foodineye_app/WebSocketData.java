package com.example.foodineye_app;

import java.util.List;

public class WebSocketData {
    private String h_id;
    private String u_id;
    private List<OrderStatus> orderStatuses;

    public List<OrderStatus> getOrderStatuses() {
        return orderStatuses;
    }

    public void setOrderStatuses(List<OrderStatus> orderStatuses) {
        this.orderStatuses = orderStatuses;
    }

    public class OrderStatus{
        private String o_id;
        private String status;

        public String getO_id() {
            return o_id;
        }

        public void setO_id(String o_id) {
            this.o_id = o_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }


}
