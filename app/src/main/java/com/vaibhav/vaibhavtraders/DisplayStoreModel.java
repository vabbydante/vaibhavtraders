package com.vaibhav.vaibhavtraders;

public class DisplayStoreModel {

    public String userStoreName;

    public DisplayStoreModel(){}

    public DisplayStoreModel(String userStoreName) {
        this.userStoreName = userStoreName;
    }

    public String getUserStoreName() {
        return userStoreName;
    }

    public void setUserStoreName(String userStoreName) {
        this.userStoreName = userStoreName;
    }
}