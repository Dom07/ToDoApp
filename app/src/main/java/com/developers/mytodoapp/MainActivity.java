package com.developers.mytodoapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.format.DateFormat;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        setTitle("Home");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, new MainFragment()).commit();

        // Alarm Manager For Notification, Every Morning at 7 AM
        setAlarmManager();
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
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (id == R.id.nav_home) {
            setTitle("Home");

            fragmentManager.beginTransaction().replace(R.id.fragment, new MainFragment()).commit();

        } else if (id == R.id.nav_completedTask) {
            setTitle("Completed Task List");
            fragmentManager.beginTransaction().replace(R.id.fragment, new CompTaskFragment()).commit();

        } else if (id == R.id.nav_insights) {
            setTitle("Insights");
            fragmentManager.beginTransaction().replace(R.id.fragment, new InsightFragment()).commit();

        } else if (id == R.id.nav_log) {
            setTitle("Log");
            fragmentManager.beginTransaction().replace(R.id.fragment, new LogFragment()).commit();
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
            db.close();
            taskHelper.close();
            Toast.makeText(getBaseContext(),"Task moved to completed task list. Swipe down to refresh",Toast.LENGTH_SHORT).show();
        }else{
            SqLiteTaskHelper taskHelper = new SqLiteTaskHelper(getBaseContext());
            String TaskName = ((CheckBox) view).getText().toString();
            SQLiteDatabase db = taskHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("TASK_STATUS", "0");
            db.update("TASK_LIST", values, "TASK_NAME='" + TaskName + "'", null);
            db.close();
            taskHelper.close();
            Toast.makeText(getBaseContext(),"Task restored to Uncompleted Task List. Swipe down to refresh",Toast.LENGTH_SHORT).show();
        }
    }

    // This method is directly set on the image-icon trash button in completed task list
    public void onDelete(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete this task?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RelativeLayout rlCompletedTask = (RelativeLayout) view.findViewById(R.id.ivTaskDelete).getParent();
                TextView tvCompletedTask = (TextView) rlCompletedTask.findViewById(R.id.tvCompletedTaskName);
                SpannableString taskName = SpannableString.valueOf(tvCompletedTask.getText().toString());
                SqLiteTaskHelper taskHelper = SqLiteTaskHelper.getInstance(getBaseContext());
                SQLiteDatabase db = taskHelper.getWritableDatabase();
                db.delete("TASK_LIST", "TASK_NAME='" + taskName + "'", null);
                Toast.makeText(getBaseContext(),"Task Deleted Successfully. Swipe down to refresh",Toast.LENGTH_SHORT).show();
                db.close();
                taskHelper.close();
                taskName.setSpan(new StrikethroughSpan(),0,taskName.length(),0);
                tvCompletedTask.setText(taskName);
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setAlarmManager(){
        boolean alarmSet = (PendingIntent.getBroadcast(getBaseContext(),100,new Intent(getBaseContext(),NotificationReceiver.class),PendingIntent.FLAG_NO_CREATE) == null);
        if(alarmSet){

            long timedifference;
            long timeInMilli;

//         make calendar instance for now and the time when we want to set the alarm
            Calendar now = Calendar.getInstance();
            Calendar alarmTime = Calendar.getInstance();

//         set hours and minutes to 7 am
            alarmTime.set(Calendar.HOUR_OF_DAY,7);
            alarmTime.set(Calendar.MINUTE,00);
            long alarmTimeMilli = alarmTime.getTimeInMillis();

//            if alarm time has passed then add 24 hours to alarm time to set it for the next day
            if(alarmTime.before(now)){
                alarmTimeMilli = alarmTimeMilli+86400000L;
                timeInMilli=alarmTimeMilli;
            }else{
                timeInMilli = now.getTimeInMillis();
            }

//            calculate the time difference between the alarm set time and now
            timedifference = timeInMilli - System.currentTimeMillis();
            long seconds = timedifference/1000;
            long minutes = seconds/60;
            long hours = minutes/60;
            long days = hours/24;

//            setting the notification manager and the alarm manager
            Intent intent = new Intent(getBaseContext(),NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),100,intent,0);
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,timeInMilli,AlarmManager.INTERVAL_HOUR,pendingIntent);
            Toast.makeText(getBaseContext(),"Time Remaining : "+days+"Days"+hours+"hours "+minutes % 60+"minutes" ,Toast.LENGTH_LONG).show();
        }else {
//            if alarm already exist just show toast
            Toast.makeText(getBaseContext(),"Alarm Service Already Exist",Toast.LENGTH_SHORT).show();
        }
    }
}
