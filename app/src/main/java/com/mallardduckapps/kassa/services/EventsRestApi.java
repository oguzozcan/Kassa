package com.mallardduckapps.kassa.services;

import com.mallardduckapps.kassa.objects.Event;
import com.mallardduckapps.kassa.objects.Expense;
import com.mallardduckapps.kassa.utils.Constants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by oguzemreozcan on 16/09/16.
 */
public class EventsRestApi {

    public interface GetEventsRestApi {
        @GET(Constants.EVENTS)
        Call<ArrayList<Event>> getEventsList(@Header("Authorization") String token, @Header("client_info") String clientInfo);
    }

    public interface PostEventRestApi{
        @POST(Constants.EVENTS)
        Call<Event> onEventCreated(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Body Event eventJsonBody);
    }

    public interface GetEventRestApi{
        @GET(Constants.EVENTS + "/" + "{eventId}")
        Call<Event> getEventWithId(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Path("eventId") long EventId);
    }

    public interface DeleteEventRestApi{
        @DELETE(Constants.EVENTS + "/"+ "{eventId}")
        Call<String> onEventDeleted(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Path("eventId") long eventId);
    }
}
