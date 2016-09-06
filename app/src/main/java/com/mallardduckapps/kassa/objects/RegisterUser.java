package com.mallardduckapps.kassa.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 01/08/16.
 */
public class RegisterUser {

    @SerializedName("FirstName")
    private String name;
    @SerializedName("LastName")
    private String surname;
    @SerializedName("EmailAddress")
    private String email;


//    @SerializedName("Password")
//    private String pass;
//    @SerializedName("ConfirmPassword")
//    private String confirmPass;

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

//    public String getNationalId() {
//        return nationalId;
//    }
//
//    public void setNationalId(String nationalId) {
//        this.nationalId = nationalId;
//    }
//
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public String getPass() {
//        return pass;
//    }
//
//    public void setPass(String pass) {
//        this.pass = pass;
//    }
//
//    public String getConfirmPass() {
//        return confirmPass;
//    }
//
//    public void setConfirmPass(String confirmPass) {
//        this.confirmPass = confirmPass;
//    }
}
