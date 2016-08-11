package com.mallardduckapps.kassa.objects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 28/07/16.
 */
public class Expense {

    @SerializedName("Id")
    private int id;
    @SerializedName("UserId")
    private int userId;
    @SerializedName("Name")
    private String name;
    @SerializedName("Price")
    private double price;
    @SerializedName("CurrencyId")
    private int currencyId;
    @SerializedName("Recurring")
    private boolean isRecurring;
    @SerializedName("DueDate")
    private String dueDate;
    @SerializedName("ReminderDate")
    private String reminderDate;
    @SerializedName("CategoryId")
    private int categoryId;
    @SerializedName("SubCategoryId")
    private int subCategoryId;
    @SerializedName("PhotoUrl")
    private String photoUrl;
    @SerializedName("TypeId")
    private int typeId;
    @SerializedName("CreateDate")
    private String createDate;
    @SerializedName("ExpenseRecurringType")
    private int recurringType;
    @SerializedName("ExpenseCoOwnerModels")
    ArrayList<CoOwner> coOwners;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getRecurringType() {
        return recurringType;
    }

    public void setRecurringType(int recurringType) {
        this.recurringType = recurringType;
    }

    public ArrayList<CoOwner> getCoOwners() {
        return coOwners;
    }

    public void setCoOwners(ArrayList<CoOwner> coOwners) {
        this.coOwners = coOwners;
    }
}
