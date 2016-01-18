package com.raccoonapps.pricemanager.app.client.model;

import android.graphics.Color;

public class Tag {

    private String id;
    private String text;
    private int isActive;
    private boolean classOrID;


    public Tag(String id, String text, boolean classOrID) {
        this.id = id;
        this.text = text;
        this.isActive = Color.parseColor("#ffffff");
        this.classOrID = classOrID;
    }

    public int isActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getIsActive() {
        return isActive;
    }

    public boolean isClassOrID() {
        return classOrID;
    }

    public void setClassOrID(boolean classOrID) {
        this.classOrID = classOrID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
