package com.mallardduckapps.kassa.services;

import com.mallardduckapps.kassa.objects.Person;
import com.mallardduckapps.kassa.objects.Picture;
import com.mallardduckapps.kassa.utils.Constants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by oguzemreozcan on 06/09/16.
 */
public class MiscRestApi {

    public interface PostProfilePic{
        @POST(Constants.PROFILE_PIC_POSTFIX)
        Call<Person> onProfilePicUploaded(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Body Picture picture);
    }
}
