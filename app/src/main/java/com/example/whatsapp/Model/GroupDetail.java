package com.example.whatsapp.Model;

public class GroupDetail {
    String name;
    String userID;
    String time;

    public GroupDetail() {
    }

    public GroupDetail(String name, String userID, String time) {
        this.name = name;
        this.userID = userID;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
