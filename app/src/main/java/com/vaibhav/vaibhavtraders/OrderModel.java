package com.vaibhav.vaibhavtraders;

public class OrderModel {

    private String date;
    private String store;
    private String orders;

    public OrderModel(){}

    public OrderModel(String date, String store, String orders) {
        this.date = date;
        this.store = store;
        this.orders = orders;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }
}
