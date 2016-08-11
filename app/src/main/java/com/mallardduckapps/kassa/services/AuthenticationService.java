package com.mallardduckapps.kassa.services;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.mallardduckapps.kassa.busevents.ApiErrorEvent;
import com.mallardduckapps.kassa.busevents.AuthEvents;
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
    private final String TAG = "AuthService";

    public AuthenticationService(Bus mBus, AuthRestApi.AuthenticationRestApi authRestApi, AuthRestApi.RegisterRestApi registerRestApi){
        this.mBus = mBus;
        this.authRestApi = authRestApi;
        this.registerRestApi = registerRestApi;
    }

    @Subscribe
    public void getAuthToken(final AuthEvents.AuthRequest event){
        authRestApi.getAuthToken(event.getUserName(), event.getPassword(), event.getGrantType(), event.getClientId()).enqueue(new Callback<AuthObject>() {
            @Override
            public void onResponse(Call<AuthObject> call, Response<AuthObject> response) {
                Log.d(TAG, "ON RESPONSE auth token: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new AuthEvents.AuthResponse(response, ""));
                } else {
                    mBus.post(new AuthEvents.AuthResponse(null, KassaUtils.getErrorMessage(response)));
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
                Log.d(TAG, "ON RESPONSE auth token: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
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
