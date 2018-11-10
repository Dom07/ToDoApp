package com.developers.mytodoapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

/**
 * Created by root on 22/3/18.
 */

public class ReminderService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       String TaskName = intent.getStringExtra("TaskName");
        int AlarmRequestCode = SqLiteTaskHelper.getAlarmRequestCode(context, TaskName);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = toString().valueOf(AlarmRequestCode);
        String channelName = TaskName+" Reminder";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel mChannel = new NotificationChannel(channelId,channelName,importance);
        mChannel.enableVibration(true);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(mChannel);

        Intent notificationIntent = new Intent(context,MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                AlarmRequestCode,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notify_small_icon)
                .setContentTitle("Reminder")
                .setContentText(TaskName)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setChannelId(channelId);

        notificationManager.notify(AlarmRequestCode, builder.build());
        SqLiteTaskHelper.updateAlarmRequestCode(context, TaskName, 0);

    }
}
