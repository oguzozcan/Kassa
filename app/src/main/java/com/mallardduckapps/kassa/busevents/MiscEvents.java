package com.mallardduckapps.kassa.busevents;

import com.mallardduckapps.kassa.objects.Expense;
import com.mallardduckapps.kassa.objects.Person;
import com.mallardduckapps.kassa.objects.Picture;

import retrofit2.Response;

/**
 * Created by oguzemreozcan on 06/09/16.
 */
public class MiscEvents {

    public static class PostProfilePicRequest{
        private final Picture picture;

        public PostProfilePicRequest(Picture picture) {
            this.picture = picture;
        }

        public Picture getPicture() {
            return picture;
        }
    }

    public static class PostProfilePicResponse{
        private Response<Person> response;

        public PostProfilePicResponse(Response<Person> response) {
            this.response = response;
        }

        public Response<Person> getPerson() {
            return response;
        }
    }

    public static class GetWebPageRequest{

    }
}
