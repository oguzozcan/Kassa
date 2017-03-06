package com.mallardduckapps.kassa.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 01/08/16.
 */
public class CoOwner implements Parcelable {

    @SerializedName("id")
    private int id;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("create_date")
    private String createDate;
    @SerializedName("user_public_profile")
    private Person person;
    @SerializedName("debt")
    private double debt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.createDate);
        dest.writeParcelable(this.person, flags);
        dest.writeDouble(this.debt);
    }

    public CoOwner() {
    }

    protected CoOwner(Parcel in) {
        this.id = in.readInt();
        this.phoneNumber = in.readString();
        this.createDate = in.readString();
        this.person = in.readParcelable(Person.class.getClassLoader());
        this.debt = in.readDouble();
    }

    public static final Parcelable.Creator<CoOwner> CREATOR = new Parcelable.Creator<CoOwner>() {
        @Override
        public CoOwner createFromParcel(Parcel source) {
            return new CoOwner(source);
        }

        @Override
        public CoOwner[] newArray(int size) {
            return new CoOwner[size];
        }
    };
}
