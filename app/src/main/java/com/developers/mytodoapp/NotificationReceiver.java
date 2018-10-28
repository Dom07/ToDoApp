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
import android.util.Log;

/**
 * Created by dom on 20/9/17.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = 100;
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context,MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,notificationId,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "channel100";
        String channelName = "Daily Reminder";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentTitle("Plan Your Day")
                .setContentText("List down the tasks you want to accomplish today")
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setSmallIcon(R.drawable.ic_notify_small_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            builder.setChannelId(channelId);
        }

        notificationManager.notify(notificationId, builder.build());
        Log.i("Morning Alarm", "True");
    }
}
