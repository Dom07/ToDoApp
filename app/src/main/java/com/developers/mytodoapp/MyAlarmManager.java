package com.developers.mytodoapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by root on 31/1/18.
 */

public class MyAlarmManager {

    Context context;

    public MyAlarmManager(Context context){
        this.context = context;
    }

    public void setMorningAlarm(){

        long timeDifference;
        int alarmHour = 7;
        int alarmMin = 1;


//         make calendar instance for now and the time when we want to set the alarm
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.setTimeInMillis(System.currentTimeMillis());

//         set hours and minutes to 7 am
        alarmTime.set(Calendar.HOUR_OF_DAY,alarmHour);
        alarmTime.set(Calendar.MINUTE,alarmMin);
        long alarmTimeMilli = alarmTime.getTimeInMillis();

//            if alarm time has passed then add 24 hours to alarm time to set it for the next day
        if(alarmTime.before(now)){
            alarmTimeMilli = alarmTimeMilli+86400000L;
        }

//            calculate the time difference between the alarm set time and now
        timeDifference = alarmTimeMilli - now.getTimeInMillis();
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(timeDifference);
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(timeDifference);
        int hours = (int) TimeUnit.MILLISECONDS.toHours(timeDifference);
        int days = (int) TimeUnit.MILLISECONDS.toDays(timeDifference);

//            setting the notification manager and the alarm manager
        Intent intent = new Intent(context,NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE) ;
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,alarmTimeMilli,AlarmManager.INTERVAL_DAY,pendingIntent);
//            Toast.makeText(getBaseContext(),"Time Remaining : "+days+"Days"+hours+"hours "+minutes+"minutes"+seconds+"seconds" ,Toast.LENGTH_LONG).show();
    }

    public void deleteDbData() {
        int alarmHour=2;
        int alarmMinutes=30;
        long timeDifferenceInMillis;
//           calendar instance of the time when we want to clear db data
        Calendar clearDbTime = Calendar.getInstance();
        clearDbTime.setTimeInMillis(System.currentTimeMillis());
        clearDbTime.set(Calendar.HOUR_OF_DAY, alarmHour);
        clearDbTime.set(Calendar.MINUTE, alarmMinutes);

//            calendar instance of current time
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());

        long clearDbTimeInMillis = clearDbTime.getTimeInMillis();

//             add another day in milliseconds if the time has already past
        if (clearDbTime.before(now)) {
            clearDbTimeInMillis = clearDbTimeInMillis + 86400000L;
        }

//            setting up the time variables
        timeDifferenceInMillis = clearDbTimeInMillis - now.getTimeInMillis();
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(timeDifferenceInMillis);
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(timeDifferenceInMillis);
        int hour = (int) TimeUnit.MILLISECONDS.toHours(timeDifferenceInMillis);
        int day = (int) TimeUnit.MILLISECONDS.toDays(timeDifferenceInMillis);

//            setting up the intent and alarm manager
        Intent intent = new Intent(context, ClearDbReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, clearDbTimeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
//            Toast.makeText(getBaseContext(), "Time For Db Clear : " + day + "Days" + hour + "hours " + min + "minutes" + sec + "seconds", Toast.LENGTH_LONG).show();
    }


    public void setReminder(long timeInMillis, int RequestCode, String TaskName){
        Intent intent = new Intent(context, ReminderService.class);
        intent.putExtra("TaskName",TaskName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
    }

    public void cancelReminder(int AlarmRequestCode, String TaskName){
        Intent intent = new Intent(context, ReminderService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,AlarmRequestCode,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
        SqLiteTaskHelper.updateAlarmRequestCode(context, TaskName, 0);
    }
}
