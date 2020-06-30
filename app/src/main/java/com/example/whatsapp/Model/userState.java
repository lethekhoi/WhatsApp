package com.example.whatsapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class userState implements Serializable {

    @SerializedName("day")
    @Expose
    String day;
    @SerializedName("state")
    @Expose
    String state;
    @SerializedName("time")
    @Expose
    String time;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public userState() {
    }

    public userState(String day, String state, String time) {
        this.day = day;
        this.state = state;
        this.time = time;
    }
}
