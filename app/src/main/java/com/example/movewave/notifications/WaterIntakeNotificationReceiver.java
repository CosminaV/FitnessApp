package com.example.movewave.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.movewave.R;
import com.example.movewave.classes.User;
import com.example.movewave.operations.UserOperations;

import java.util.Calendar;

public class WaterIntakeNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UserOperations.loadUserFromFirestore(new UserOperations.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                if (user!=null) {
                    int currentWaterIntake = user.getWaterIntake();
                    Calendar currentTime = Calendar.getInstance();
                    int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = currentTime.get(Calendar.MINUTE);

                    if (currentWaterIntake < 100 && hour == 21 && minute == 16) {
                        showNotification(context, currentWaterIntake);
                    }
                }
            }
        });
    }

    private void showNotification(Context context, int currentWaterIntake) {
        String title = context.getString(R.string.waterNotifTitle);
        String contentText =
                context.getString(R.string.waterNotifContent, currentWaterIntake);
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder builder = notificationHelper.getChannelNotification(title, contentText);
        builder.setSmallIcon(R.drawable.ic_baseline_water_drop_24)
                .setAutoCancel(false);
        notificationHelper.getManager().notify(2, builder.build());
    }
}
