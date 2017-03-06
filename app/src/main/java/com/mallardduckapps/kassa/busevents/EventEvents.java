package com.mallardduckapps.kassa.busevents;

import com.mallardduckapps.kassa.objects.Event;

import java.util.ArrayList;

import retrofit2.Response;

/**
 * Created by oguzemreozcan on 16/09/16.
 */
public class EventEvents {

    public static class EventListRequest {
    }

    public static class EventListResponse {
        private Response<ArrayList<Event>> response;

        public EventListResponse(Response<ArrayList<Event>> response) {
            this.response = response;
        }

        public Response<ArrayList<Event>> getResponse() {
            return response;
        }
    }

    public static class PostEventRequest {
        private final Event Event;

        public PostEventRequest(Event Event) {
            this.Event = Event;
        }

        public Event getItem() {
            return Event;
        }
    }

    public static class PostEventResponse {
        private Response<Event> response;

        public PostEventResponse(Response<Event> response) {
            this.response = response;
        }

        public Response<Event> getItem() {
            return response;
        }
    }

    public static class GetEventRequest {
        private int EventId;

        public GetEventRequest(int EventId) {
            this.EventId = EventId;
        }

        public int getEventId() {
            return EventId;
        }
    }

    public static class GetEventResponse {
        private Response<Event> response;

        public GetEventResponse(Response<Event> response) {
            this.response = response;
        }

        public Response<Event> getItem() {
            return response;
        }
    }

    public static class DeleteEventRequest {
        private int EventId;

        public DeleteEventRequest(int EventId) {
            this.EventId = EventId;
        }

        public int getEventId() {
            return EventId;
        }
    }

    public static class DeleteEventResponse {
        private Response<String> response;

        public DeleteEventResponse(Response<String> response) {
            this.response = response;
        }

        public Response<String> getItem() {
            return response;
        }
    }
}
