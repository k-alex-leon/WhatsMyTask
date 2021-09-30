package com.example.whatsmytask.providers;

import com.example.whatsmytask.models.FCMBody;
import com.example.whatsmytask.models.FCMResponse;
import com.example.whatsmytask.retrofit.IFCMApi;
import com.example.whatsmytask.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider(){}

    public Call<FCMResponse> sendNotification(FCMBody body){
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
