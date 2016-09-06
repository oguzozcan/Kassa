package com.mallardduckapps.kassa.services;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.mallardduckapps.kassa.busevents.ApiErrorEvent;
import com.mallardduckapps.kassa.busevents.AuthEvents;
import com.mallardduckapps.kassa.objects.Session;
import com.mallardduckapps.kassa.objects.User;
import com.mallardduckapps.kassa.utils.KassaUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 28/07/16.
 */
public class AuthenticationService {

    private Bus mBus;
    private AuthRestApi.AuthenticationRestApi authRestApi;
    private AuthRestApi.RegisterRestApi registerRestApi;
    private AuthRestApi.GetConfirmationCode getConfirmationCodeApi;
    private AuthRestApi.PostConfirmationCode postConfirmationCodeApi;
    private final String TAG = "AuthService";

    public AuthenticationService(Bus mBus, AuthRestApi.AuthenticationRestApi authRestApi, AuthRestApi.RegisterRestApi registerRestApi, AuthRestApi.GetConfirmationCode getConfirmationCodeApi, AuthRestApi.PostConfirmationCode postConfirmationCodeApi){
        this.mBus = mBus;
        this.authRestApi = authRestApi;
        this.registerRestApi = registerRestApi;
        this.getConfirmationCodeApi = getConfirmationCodeApi;
        this.postConfirmationCodeApi = postConfirmationCodeApi;
    }

    @Subscribe
    public void getAuthToken(final AuthEvents.AuthRequest event){
        authRestApi.getAuthToken(event.getPhoneNumber(), event.getSessionId(), event.getGrantType(), event.getClientId()).enqueue(new Callback<AuthObject>() {
            @Override
            public void onResponse(Call<AuthObject> call, Response<AuthObject> response) {
                Log.d(TAG, "ON RESPONSE auth token: " + response.isSuccessful() + " - responsecode: " + response.code() +
                        " - response:" + response.message());
                Log.d(TAG, "On response phoneNumber : " + event.getPhoneNumber() + " - sessionİd: " + event.getSessionId());
                if (response.isSuccessful()) {
                    mBus.post(new AuthEvents.AuthResponse(response, "", event.getPhoneNumber(), event.isExistingUser()));
                } else {
                    mBus.post(new AuthEvents.AuthResponse(null, KassaUtils.getErrorMessage(response), event.getPhoneNumber(), event.isExistingUser()));
                    //mBus.post(new ApiErrorEvent(response.code(), KassaUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<AuthObject> call, Throwable t) {
                Log.d(TAG, "ON FAILURE: " + t.getMessage());
                t.printStackTrace();
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void getRegisteredUser(final AuthEvents.RegisterRequest registerRequest ){
        registerRestApi.registerUser(registerRequest.getRegisterationObject()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "ON RESPONSE get registered user: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new AuthEvents.RegisterResponse(response));
                } else {
                    mBus.post(new AuthEvents.RegisterResponse(null));
                    mBus.post(new ApiErrorEvent(response.code(), KassaUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "ON FAILURE: " + t.getMessage());
                t.printStackTrace();
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void getConfirmationCode(final AuthEvents.GetConfirmationCodeRequest getConfirmationCodeRequest ){
        final String phoneNumber = getConfirmationCodeRequest.getPhoneNumber();
        getConfirmationCodeApi.getConfirmationCode(phoneNumber).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "ON RESPONSE getConf Code: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + " - url : " + call.request().url() + " phone: " + phoneNumber);
                if (response.isSuccessful()) {
                    mBus.post(new AuthEvents.GetConfirmationCodeResponse(response, phoneNumber));
                } else {
                    mBus.post(new AuthEvents.GetConfirmationCodeResponse(null, phoneNumber));
                    mBus.post(new ApiErrorEvent(response.code(), KassaUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "ON FAILURE get conf code: " + t.getMessage() + " - " +  call.request().body());
                t.printStackTrace();
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void postConfirmationCode(final AuthEvents.PostConfirmationCodeRequest postConfirmationCodeRequest ){
        final String phoneNumber = postConfirmationCodeRequest.getPhoneNumber();
        postConfirmationCodeApi.getAppSession(phoneNumber, postConfirmationCodeRequest.getConfirmationCode()).enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {
                Log.d(TAG, "ON RESPONSE post conf code: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + "- phoneNumber: " + phoneNumber);
                if (response.isSuccessful()) {
                    mBus.post(new AuthEvents.PostConfirmationCodeResponse(response, phoneNumber));
                } else {
                    mBus.post(new AuthEvents.PostConfirmationCodeResponse(null, phoneNumber));
                    mBus.post(new ApiErrorEvent(response.code(), KassaUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {
                Log.d(TAG, "ON FAILURE: " + t.getMessage());
                t.printStackTrace();
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    public class AuthObject{
        @SerializedName("access_token")
        private String accessToken;
        @SerializedName("token_type")
        private String tokenType;
        @SerializedName("expires_in")
        private long expiresIn;

        public String getAccessToken() {
            return accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public long getExpiresIn() {
            return expiresIn;
        }
    }
}
