package com.mallardduckapps.kassa.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 28/07/16.
 */
public class Expense extends BaseSwipeListItem implements Parcelable {

    @SerializedName("price")
    private double price;
    @SerializedName("currency_id")
    private int currencyId;
    @SerializedName("recurring")
    private boolean isRecurring;
    @SerializedName("due_date")
    private String dueDate;
    @SerializedName("reminder_date")
    private String reminderDate;
    @SerializedName("photo_url")
    private String photoUrl;
    @SerializedName("expense_recurring_type")
    private int recurringType;
    @SerializedName("expense_co_owner_models")
    ArrayList<CoOwner> coOwners;

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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeDouble(this.price);
        dest.writeInt(this.currencyId);
        dest.writeByte(this.isRecurring ? (byte) 1 : (byte) 0);
        dest.writeString(this.dueDate);
        dest.writeString(this.reminderDate);
        dest.writeString(this.photoUrl);
        dest.writeInt(this.recurringType);
        dest.writeList(this.coOwners);
    }

    public Expense() {
    }

    protected Expense(Parcel in) {

        this.price = in.readDouble();
        this.currencyId = in.readInt();
        this.isRecurring = in.readByte() != 0;
        this.dueDate = in.readString();
        this.reminderDate = in.readString();
        this.photoUrl = in.readString();
        this.recurringType = in.readInt();
        this.coOwners = new ArrayList<CoOwner>();
        in.readList(this.coOwners, CoOwner.class.getClassLoader());
    }

    public static final Parcelable.Creator<Expense> CREATOR = new Parcelable.Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel source) {
            return new Expense(source);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };
}
