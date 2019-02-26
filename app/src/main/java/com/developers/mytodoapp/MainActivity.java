package com.developers.mytodoapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;

    EditText etNewTaskName;
    TextView tvReminder;
    Switch switchReminder;
    int mHour = 0, mMinute;
    long reminderTimeInMillis;

    SqLiteTaskHelper taskHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_main);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTaskAlertBox();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, new MainFragment()).commit();

        // Alarm Manager For Notification, Every Morning at 7 AM
        MyAlarmManager myAlarmManager = new MyAlarmManager(getBaseContext());
        myAlarmManager.setMorningAlarm();
        // DataBase Clearer
        myAlarmManager.deleteDbData();
    }

    public void newTaskAlertBox(){
        LayoutInflater inflater = getLayoutInflater();
        View alertBox = inflater.inflate(R.layout.newtask, null);
        etNewTaskName = (EditText) alertBox.findViewById(R.id.etNewTaskName);
        tvReminder = (TextView)alertBox.findViewById(R.id.tvReminder);
        switchReminder =(Switch)alertBox.findViewById(R.id.switchReminder);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Task");
        builder.setView(alertBox);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                rlAddTaskContainer.setVisibility(View.VISIBLE);
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                rlAddTaskContainer.setVisibility(View.VISIBLE);
            }
        });

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainFragment mainFragment = new MainFragment();
                mainFragment.insertNewTask(etNewTaskName.getText().toString(),switchReminder.isChecked());
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimationUpDown;
        dialog.show();
    }

}
