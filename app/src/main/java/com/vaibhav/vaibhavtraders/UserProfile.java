package com.vaibhav.vaibhavtraders;

public class UserProfile {

    public String userName;
    public String userNumber;
    public String userStoreName;
    public String userFcm;

    public UserProfile(){}

    public UserProfile(String userName, String userNumber, String userStoreName, String userFcm) {  //used in registration. in NextRegistrationActivity.java
        this.userName = userName;
        this.userNumber = userNumber;
        this.userStoreName = userStoreName;
        this.userFcm = userFcm;
    }

    /*public UserProfile(String userName, String userNumber, String userStoreName) { //used in UpdateActivity.java
        this.userName = userName;
        this.userNumber = userNumber;
        this.userStoreName = userStoreName;
    }*/

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserStoreName() {
        return userStoreName;
    }

    public void setUserStoreName(String userStoreName) {
        this.userStoreName = userStoreName;
    }

    public String getUserFcm() {
        return userFcm;
    }

    public void setUserFcm(String userFcm) {
        this.userFcm = userFcm;
    }
}
