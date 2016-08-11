package com.mallardduckapps.kassa.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 01/08/16.
 */
public class CoOwner {

    @SerializedName("Id")
    private int id;
    @SerializedName("UserId")
    private int userId;
    @SerializedName("CreateDate")
    private String createDate;
    @SerializedName("UserPublicProfile")
    private Person person;
    @SerializedName("Debt")
    private double debt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }
}
