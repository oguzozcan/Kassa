package com.mallardduckapps.kassa.busevents;

import android.util.Log;

import com.mallardduckapps.kassa.objects.RegisterUser;
import com.mallardduckapps.kassa.objects.Session;
import com.mallardduckapps.kassa.objects.User;
import com.mallardduckapps.kassa.services.AuthenticationService;

import retrofit2.Response;

/**
 * Created by oguzemreozcan on 10/08/16.
 */
public class AuthEvents {

    public static class AuthRequest {

//        private String userName;
//        private String password;
        private boolean isExistingUser;
        private String phoneNumber;
        private String sessionId;
        private String grantType;
        private String clientId;

        public AuthRequest(boolean isExistingUser, String phoneNumber, String sessionId, String grantType, String clientId) {
//            this.userName = userName;
//            this.password = password;
            this.isExistingUser = isExistingUser;
            this.phoneNumber = phoneNumber;
            this.sessionId = sessionId;
            this.grantType = grantType;
            this.clientId = clientId;
        }

//        public String getUserName() {
//            return userName;
//        }
//
//        public String getPassword() {
//            return password;
//        }


        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getSessionId() {
            return sessionId;
        }

        public boolean isExistingUser() {
            return isExistingUser;
        }

        public String getGrantType() {
            return grantType;
        }

        public String getClientId() {
            return clientId;
        }
    }

    public static class AuthResponse {

        private Response<AuthenticationService.AuthObject> response;
        private String phoneNumber;
        private String message;
        private boolean isExistingUser;

        public AuthResponse(Response<AuthenticationService.AuthObject> response, String message, String phoneNumber, boolean isExistingUser) {
            this.response = response;
            this.message = message;
            this.phoneNumber = phoneNumber;
            this.isExistingUser = isExistingUser;
        }

        public Response<AuthenticationService.AuthObject> getResponse() {
            return response;
        }

        public String getMessage() {
            return message;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public boolean isExistingUser() {
            return isExistingUser;
        }
    }

    public static class RegisterRequest{
        private final RegisterUser register;

        public RegisterRequest(RegisterUser register) {
            this.register = register;
        }

        public RegisterUser getRegisterationObject() {
            return register;
        }
    }

    public static class RegisterResponse{
        private Response<User> response;

        public RegisterResponse(Response<User> response) {
            this.response = response;
        }

        public Response<User> getUser() {
            return response;
        }
    }


    public static class GetConfirmationCodeRequest{

        private String phoneNumber;

        public GetConfirmationCodeRequest(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

    }

    public static class GetConfirmationCodeResponse{
        private Response<Void> response;
        private String phoneNumber;

        public GetConfirmationCodeResponse(Response<Void> response, String phoneNumber) {
            this.response = response;
            this.phoneNumber = phoneNumber;
        }

        public Response<Void> getResponse() {
            return response;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }
    }

    public static class PostConfirmationCodeRequest{

        private String phoneNumber;
        private String confirmationCode;

        public PostConfirmationCodeRequest(String phoneNumber, String confirmationCode) {
            this.phoneNumber = phoneNumber;
            this.confirmationCode = confirmationCode;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getConfirmationCode() {
            return confirmationCode;
        }

    }

    public static class PostConfirmationCodeResponse{
        private Response<Session> response;
        private String phoneNumber;

        public PostConfirmationCodeResponse(Response<Session> response, String phoneNumber) {
            this.response = response;
            this.phoneNumber = phoneNumber;
            Log.d("AUTH EVENTS", "PHONE NUMBER SAVED: " + phoneNumber);
        }

        public Response<Session> getResponse() {
            return response;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }
    }
}
