package com.example.whatsapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserProfile implements Serializable {


    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("image")
    @Expose
    String image;
    @SerializedName("uid")
    @Expose
    String uid;
    @SerializedName("status")
    @Expose
    String status;

    public UserProfile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserProfile(String name, String image, String uid, String status) {
        this.name = name;
        this.image = image;
        this.uid = uid;
        this.status = status;
    }
}
