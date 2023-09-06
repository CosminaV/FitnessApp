package com.example.movewave.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.movewave.RemindersActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra(RemindersActivity.SEND_TITLE);
        String contentText = intent.getStringExtra(RemindersActivity.SEND_CONTENT_TEXT);
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder builder = notificationHelper.getChannelNotification(title, contentText);
        notificationHelper.getManager().notify(1, builder.build());
    }
}
