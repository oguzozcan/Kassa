package com.mallardduckapps.kassa.utils;

/**
 * Created by oguzemreozcan on 28/07/16.
 */
public class Constants {

    public final static String ROOT_URL = "http://52.206.14.167/v1/";
    public final static String AUTH_URL = "http://52.206.14.167:8081/";

    public final static String PHOTO_BASE_URL = "http://52.206.14.167:8091/ProfilePictures/";

    public final static String EXPENSE = "expenses";

    public final static String ACCESS_TOKEN_KEY = "ACCESS_TOKEN";
    public final static String VISITOR_ACCESS_TOKEN_KEY = "VISITOR_ACCESS_TOKEN";
    public final static String SESSION_ID = "SESSION_ID";
    public final static String USERNAME = "USERNAME";
    public final static String PHONE_NUMBER = "PHONE_NUMBER";
    public final static String USERID = "USER_ID";

    public final static int CATEGORY_ID_DAILY = 1;
    public final static int CATEGORY_ID_WORK = 2;
    public final static int CATEGORY_ID_HOME = 3;
    public final static int CATEGORY_ID_EVENT = 4;

    //PERMISSIONS
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    public static final int MY_PERMISSIONS_READ_CONTACTS = 2;
    public static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 3;
    public static final int MY_PERMISSIONS_CALL = 4;

    public final static int NO_CONNECTION = -99;

    //TODO THESE SHOULD CHANGE
    public final static String CLIENT_ID ="8fa39c113b404035bf55b84d22d65053";
    //public final static String CLIENT_SECRET ="ithN_jkZzWBSi59tOthbnhvMPzFGVM8Ob-_rBzZv8RA";

    public final static int EXPENSE_CREATED = 100;
    public final static int EXPENSE_UPDATED = 101;

    //TypeIds
    //Daily
    public final static int TYPE_SHOPPING = 1;
    public final static int TYPE_ELECTRONIC = 2;
    public final static int TYPE_HOBBY = 3;
    public final static int TYPE_PERSONALCARE = 4;
    public final static int TYPE_MARKET = 5;
    public final static int TYPE_GAME = 6;
    public final static int TYPE_TRANSPORTATION = 7;
    public final static int TYPE_DAILY_OTHER = 8;

//    public final static int TYPE_SHOPPING = 1;
//    public final static int TYPE_ELECTRONIC = 2;
//    public final static int TYPE_HOBBY = 3;
//    public final static int TYPE_PERSONALCARE = 4;
//    public final static int TYPE_MARKET = 5;
//    public final static int TYPE_GAME = 6;
//    public final static int TYPE_TRANSPORTATION = 7;
//    public final static int TYPE_DAILY_OTHER = 8;


}
