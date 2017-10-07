package com.developers.mytodoapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set the default home fragment
        setTitle("Today's To-Do List");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, new MainFragment()).commit();

        // Alarm Manager For Notification, Every Morning at 7 AM
        setAlarmManager();
        // DataBase Clearer
        deleteDbData();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    // To navigate through fragments
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (id == R.id.nav_home) {
            setTitle("Today's To-Do List");

            fragmentManager.beginTransaction().replace(R.id.fragment, new MainFragment()).commit();

        } else if (id == R.id.nav_completedTask) {
            setTitle("Completed Task List");
            fragmentManager.beginTransaction().replace(R.id.fragment, new CompTaskFragment()).commit();

        } else if (id == R.id.nav_insights) {
            setTitle("Insights");
            fragmentManager.beginTransaction().replace(R.id.fragment, new InsightFragment()).commit();

        } else if (id == R.id.nav_yesterdaysPending) {
            setTitle("Yesterday's Incomplete Task");
            fragmentManager.beginTransaction().replace(R.id.fragment, new YesterdaysPendingFragment()).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Invokes when the checkbox is toggled
    public void onSelect(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if (checked) {
                SqLiteTaskHelper taskHelper = SqLiteTaskHelper.getInstance(getBaseContext());
                String TaskName = ((CheckBox) view).getText().toString();
                SQLiteDatabase db = taskHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("TASK_STATUS", "1");
                db.update("TASK_LIST", values, "TASK_NAME='" + TaskName + "'", null);
//                Log.d("TASK_LIST", "Task " + TaskName + " status set to 1");
                db.close();
                taskHelper.close();
                Toast.makeText(getBaseContext(), "Good Job! ", Toast.LENGTH_SHORT).show();
        }else {
                SqLiteTaskHelper taskHelper = new SqLiteTaskHelper(getBaseContext());
                String TaskName = ((CheckBox) view).getText().toString();
                SQLiteDatabase db = taskHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("TASK_STATUS", "0");
                db.update("TASK_LIST", values, "TASK_NAME='" + TaskName + "'", null);
                db.close();
                taskHelper.close();
                Toast.makeText(getBaseContext(), "Task restored", Toast.LENGTH_SHORT).show();
        }
    }

    public void setAlarmManager(){
        boolean alarmSet = (PendingIntent.getBroadcast(getBaseContext(),100,new Intent(getBaseContext(),NotificationReceiver.class),PendingIntent.FLAG_NO_CREATE) == null);
        if(alarmSet){

//            long timeDifference;
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
//            timeDifference = alarmTimeMilli - now.getTimeInMillis();
//            int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(timeDifference);
//            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(timeDifference);
//            int hours = (int) TimeUnit.MILLISECONDS.toHours(timeDifference);
//            int days = (int) TimeUnit.MILLISECONDS.toDays(timeDifference);

//            setting the notification manager and the alarm manager
            Intent intent = new Intent(getBaseContext(),NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,alarmTimeMilli,AlarmManager.INTERVAL_DAY,pendingIntent);
//            Log.d("AlarmManager","Time Remaining for Notification Reminder: "+days+"Days"+hours+"hours "+minutes+"minutes"+seconds+"seconds");
//            Toast.makeText(getBaseContext(),"Time Remaining : "+days+"Days"+hours+"hours "+minutes+"minutes"+seconds+"seconds" ,Toast.LENGTH_LONG).show();
        }else {
//            if alarm already exist just show toast
//            Log.d("AlarmManager","Alarm Service Already Exist");
//            Toast.makeText(getBaseContext(),"Alarm Service Already Exist",Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteDbData() {
        boolean check = (PendingIntent.getBroadcast(getBaseContext(), 0, new Intent(getBaseContext(), ClearDbReceiver.class), PendingIntent.FLAG_NO_CREATE)) == null;
        if (check) {
            int alarmHour=2;
            int alarmMinutes=30;
//            long timeDifferenceInMillis;
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
//            timeDifferenceInMillis = clearDbTimeInMillis - now.getTimeInMillis();
//            int sec = (int) TimeUnit.MILLISECONDS.toSeconds(timeDifferenceInMillis);
//            int min = (int) TimeUnit.MILLISECONDS.toMinutes(timeDifferenceInMillis);
//            int hour = (int) TimeUnit.MILLISECONDS.toHours(timeDifferenceInMillis);
//            int day = (int) TimeUnit.MILLISECONDS.toDays(timeDifferenceInMillis);

//            setting up the intent and alarm manager
            Intent intent = new Intent(getBaseContext(), ClearDbReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, clearDbTimeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
//            Log.d("AlarmManager","Time Remaining For Db Clear : " + day + "Days" + hour + "hours " + min + "minutes" + sec + "seconds");
//            Toast.makeText(getBaseContext(), "Time For Db Clear : " + day + "Days" + hour + "hours " + min + "minutes" + sec + "seconds", Toast.LENGTH_LONG).show();
        }else{
//                Log.d("AlarmManager","DbClear Service Already Exist");
//            if service already exist
//            Toast.makeText(getBaseContext(),"DbClear Service Already Exist",Toast.LENGTH_SHORT).show();
        }
    }
}
