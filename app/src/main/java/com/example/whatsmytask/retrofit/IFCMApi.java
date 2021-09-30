package com.example.whatsmytask.retrofit;

import com.example.whatsmytask.models.FCMBody;
import com.example.whatsmytask.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA6vWGhpM:APA91bHBtPfxPpsRIOpdlUJUwEIknWxbTPO5lO65wwHfiE22r1bV5N4EaC8xr7hGPtuUFF0indoBOhM0gPxN1fJpYkLN3dAwT4krNzPofIIvvinRFdtUKahPSgehoXWnvGrmkFVuJ0V-"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
