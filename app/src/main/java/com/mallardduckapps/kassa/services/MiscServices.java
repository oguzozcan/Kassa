package com.mallardduckapps.kassa.services;

import android.util.Log;

import com.mallardduckapps.kassa.KassaApp;
import com.mallardduckapps.kassa.busevents.ApiErrorEvent;
import com.mallardduckapps.kassa.busevents.MiscEvents;
import com.mallardduckapps.kassa.objects.Person;
import com.mallardduckapps.kassa.utils.KassaUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 06/09/16.
 */
public class MiscServices {
    private final Bus mBus;
    private final KassaApp app;
    private final MiscRestApi.PostProfilePic postPicRestApi;
    private final String TAG = "MiscServices";

    public MiscServices(KassaApp app, MiscRestApi.PostProfilePic postPicRestApi){
        this.app = app;
        mBus = app.getBus();
        this.postPicRestApi = postPicRestApi;
    }

    @Subscribe
    public void postProfilePic(final MiscEvents.PostProfilePicRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        postPicRestApi.onProfilePicUploaded(token, "", event.getPicture()).enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                Log.d(TAG, "ON RESPONSE postProfilePic: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + "- url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new MiscEvents.PostProfilePicResponse(response));
                } else {
                    //mBus.post(new AuthResponse(null, KassaUtils.getErrorMessage(response)));
                    mBus.post(new ApiErrorEvent(response.code(), KassaUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                Log.d(TAG, "ON FAILURE: " + t.getMessage());
                t.printStackTrace();
                mBus.post(new ApiErrorEvent());
            }
        });
    }
}
