package com.mallardduckapps.kassa.services;

import com.mallardduckapps.kassa.objects.Expense;
import com.mallardduckapps.kassa.objects.RegisterUser;
import com.mallardduckapps.kassa.objects.Session;
import com.mallardduckapps.kassa.objects.User;
import com.mallardduckapps.kassa.utils.Constants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by oguzemreozcan on 28/07/16.
 */
public class AuthRestApi {

    public interface AuthenticationRestApi{
        @FormUrlEncoded
        @POST("oauth2/token")
        Call<AuthenticationService.AuthObject> getAuthToken(@Field("username") String username, @Field("password") String password,
                                                            @Field("grant_type") String grantType, @Field("client_id") String clientId);
    }

    public interface RegisterRestApi{
        @POST("account/register")
        Call<User> registerUser(@Body RegisterUser registerJsonBody);
    }


    public interface GetConfirmationCode{
        @GET("account/confirmationCode/" + "{phoneNumber}")
        Call<Void> getConfirmationCode(@Path("phoneNumber") String phoneNumber);
    }

    public interface PostConfirmationCode{
        @POST("account/confirmationCode/" + "{phoneNumber}")
        Call<Session> getAppSession(@Path("phoneNumber") String phoneNumber, @Query("confirmationCode") String confirmationCode);
    }
}
