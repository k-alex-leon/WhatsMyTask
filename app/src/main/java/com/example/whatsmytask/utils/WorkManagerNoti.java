package com.example.whatsmytask.utils;


import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.whatsmytask.models.FCMBody;
import com.example.whatsmytask.models.FCMResponse;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.NotificationProvider;
import com.example.whatsmytask.providers.TokenProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkManagerNoti extends Worker {

    NotificationProvider mNotificationProvider;
    Context context;
    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider = new AuthProvider();


    public WorkManagerNoti(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static void saveNoti(long duration, Data data, String tag){
        OneTimeWorkRequest noti = new OneTimeWorkRequest.Builder(WorkManagerNoti.class)
                .setInitialDelay(duration, TimeUnit.MILLISECONDS).addTag(tag)
                .setInputData(data).build();

        WorkManager instance = WorkManager.getInstance();
        instance.enqueue(noti);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {

        String idUserProfile = mAuthProvider.getUid();
        mTokenProvider.getToken(idUserProfile).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "Task time!");
                        data.put("body", "you have a pending task.");
                        FCMBody body = new FCMBody(token, "high", "4500s", data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if (response.body() != null) {
                                    if (response.body().getSuccess() == 1) {
                                        Toast.makeText(context, "Message sent", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Error with message sent", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Error with message sent", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(context, "Token doesn't exist", Toast.LENGTH_SHORT).show();
                }
            }

        });

        return Result.success();

    }


}
