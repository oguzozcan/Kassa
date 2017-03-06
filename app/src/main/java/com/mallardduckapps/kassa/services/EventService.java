package com.mallardduckapps.kassa.services;

import android.util.Log;

import com.mallardduckapps.kassa.KassaApp;
import com.mallardduckapps.kassa.busevents.ApiErrorEvent;
import com.mallardduckapps.kassa.busevents.EventEvents;
import com.mallardduckapps.kassa.objects.Event;
import com.mallardduckapps.kassa.utils.KassaUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 16/09/16.
 */
public class EventService {

    private final Bus mBus;
    private final KassaApp app;
    private EventsRestApi.GetEventsRestApi getEventListRestApi;
    private EventsRestApi.GetEventRestApi getEventRestApi;
    private EventsRestApi.DeleteEventRestApi deleteEventRestApi;
    private EventsRestApi.PostEventRestApi postEventRestApi;
    private final String TAG = "EventService";

    public EventService(KassaApp app, EventsRestApi.GetEventsRestApi getEventListRestApi,
                          EventsRestApi.GetEventRestApi getEventRestApi, EventsRestApi.PostEventRestApi postEventRestApi,
                          EventsRestApi.DeleteEventRestApi deleteEventRestApi){
        this.app = app;
        this.mBus = app.getBus();
        this.getEventListRestApi = getEventListRestApi;
        this.getEventRestApi = getEventRestApi;
        this.postEventRestApi = postEventRestApi;
        this.deleteEventRestApi = deleteEventRestApi;
    }

    @Subscribe
    public void getEventList(final EventEvents.EventListRequest event){
        Log.d(TAG, "GET EVENT LIST");
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        getEventListRestApi.getEventsList(token, "").enqueue(new Callback<ArrayList<Event>>() {
            @Override
            public void onResponse(Call<ArrayList<Event>> call, Response<ArrayList<Event>> response) {
                Log.d(TAG, "ON RESPONSE EventList: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + "- url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new EventEvents.EventListResponse(response));
                } else {
                    //mBus.post(new AuthResponse(null, KassaUtils.getErrorMessage(response)));
                    mBus.post(new ApiErrorEvent(response.code(), KassaUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Event>> call, Throwable t) {
                Log.d(TAG, "ON FAILURE: " + t.getMessage());
                t.printStackTrace();
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void postEvent(final EventEvents.PostEventRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        postEventRestApi.onEventCreated(token, "", event.getItem()).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                Log.d(TAG, "ON RESPONSE postEvent: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + "- url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new EventEvents.PostEventResponse(response));
                } else {
                    //mBus.post(new AuthResponse(null, KassaUtils.getErrorMessage(response)));
                    mBus.post(new ApiErrorEvent(response.code(), KassaUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.d(TAG, "ON FAILURE: " + t.getMessage());
                t.printStackTrace();
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void deleteEvent(final EventEvents.DeleteEventRequest event){
        String token = app.isTokenPresent(TAG);
        if(token == null){
            return;
        }
        deleteEventRestApi.onEventDeleted(token, "", event.getEventId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "ON RESPONSE delete Event: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + "- url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new EventEvents.DeleteEventResponse(response));
                } else {
                    //mBus.post(new AuthResponse(null, KassaUtils.getErrorMessage(response)));
                    mBus.post(new ApiErrorEvent(response.code(), KassaUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "ON FAILURE: " + t.getMessage());
                t.printStackTrace();
                mBus.post(new ApiErrorEvent());
            }
        });
    }
}
