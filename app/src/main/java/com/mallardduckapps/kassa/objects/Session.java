package com.mallardduckapps.kassa.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 05/09/16.
 */
public class Session {

    @SerializedName("ApplicationSession")
    private String appSession;

    @SerializedName("UserExists")
    private boolean userExists;

    public String getAppSession() {
        return appSession;
    }

    public void setAppSession(String appSession) {
        this.appSession = appSession;
    }

    public boolean isUserExists() {
        return userExists;
    }

    public void setUserExists(boolean userExists) {
        this.userExists = userExists;
    }
}
