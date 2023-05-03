package com.example.foodineye_app;

import java.util.List;

//상위 리사이클러뷰 주문내역을 정의
public class Order {
    private String storeId;
    private String storeName;
    private String menuId;
    //하위 리사이클러뷰 아이템으로 정의한 subOrderList를 전역변수 선언
    private List<SubOrder> subOrderList;

    public Order(String storeId, String storeName, String menuId, List<SubOrder> subOrderList) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.menuId = menuId;
        this.subOrderList = subOrderList;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public List<SubOrder> getSubOrderList() {
        return subOrderList;
    }

    public void setSubOrderList(List<SubOrder> subOrderList) {
        this.subOrderList = subOrderList;
    }
}
