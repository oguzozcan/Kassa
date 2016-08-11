package com.mallardduckapps.kassa.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 01/08/16.
 */
public class Person {
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
}
