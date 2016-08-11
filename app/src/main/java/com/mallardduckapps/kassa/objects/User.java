package com.mallardduckapps.kassa.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 01/08/16.
 */
public class User extends Person {

    @SerializedName("Id")
    private int id;
    @SerializedName("CreateDate")
    private String createDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
