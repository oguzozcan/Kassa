package com.mallardduckapps.kassa.busevents;

import com.mallardduckapps.kassa.objects.RegisterUser;
import com.mallardduckapps.kassa.objects.User;
import com.mallardduckapps.kassa.services.AuthenticationService;

import retrofit2.Response;

/**
 * Created by oguzemreozcan on 10/08/16.
 */
public class AuthEvents {

    public static class AuthRequest {

        private String userName;
        private String password;
        private String grantType;
        private String clientId;

        public AuthRequest(String userName, String password, String grantType, String clientId) {
            this.userName = userName;
            this.password = password;
            this.grantType = grantType;
            this.clientId = clientId;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
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
        private String message;

        public AuthResponse(Response<AuthenticationService.AuthObject> response, String message) {
            this.response = response;
        }

        public Response<AuthenticationService.AuthObject> getResponse() {
            return response;
        }

        public String getMessage() {
            return message;
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
}
