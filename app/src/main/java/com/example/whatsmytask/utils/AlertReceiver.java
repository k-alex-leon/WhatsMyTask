package com.example.whatsmytask.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.whatsmytask.channel.NotificationHelper;
import com.example.whatsmytask.models.TaskU;
import com.example.whatsmytask.providers.TaskProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class AlertReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getNotification("Pending task!", "You have something pending");
        notificationHelper.getManager().notify(1,nb.build());
    }
}
