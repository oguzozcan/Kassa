package com.mallardduckapps.kassa.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 01/08/16.
 */
public class Person implements Parcelable {
    @SerializedName("FirstName")
    private String name;
    @SerializedName("LastName")
    private String surname;
    @SerializedName("NationalIdentityNumber")
    private String nationalId;
    @SerializedName("PhoneNumber")
    private String phoneNumber;
    @SerializedName("EmailAddress")
    private String email;
    @SerializedName("PhotoUrl")
    private String photoUrl;
    @SerializedName("CreateDate")
    private String createDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Person() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.surname);
        dest.writeString(this.nationalId);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.email);
        dest.writeString(this.photoUrl);
        dest.writeString(this.createDate);
    }

    protected Person(Parcel in) {
        this.name = in.readString();
        this.surname = in.readString();
        this.nationalId = in.readString();
        this.phoneNumber = in.readString();
        this.email = in.readString();
        this.photoUrl = in.readString();
        this.createDate = in.readString();
    }

    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}
