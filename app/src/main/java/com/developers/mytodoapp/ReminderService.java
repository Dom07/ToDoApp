package com.developers.mytodoapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by root on 22/3/18.
 */

public class ReminderService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String TaskName = intent.getStringExtra("TaskName");
        int AlarmRequestCode = SqLiteTaskHelper.getAlarmRequestCode(context, TaskName);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context,MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,AlarmRequestCode,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentTitle("Reminder")
                .setContentText(TaskName)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setSmallIcon(R.drawable.ic_reminder_small_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        notificationManager.notify(AlarmRequestCode, builder.build());
        SqLiteTaskHelper.updateAlarmRequestCode(context, TaskName, 0);
        Log.d("AlarmManager","Notification for task : "+ TaskName);
    }
}
