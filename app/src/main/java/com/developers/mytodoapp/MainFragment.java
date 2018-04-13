package com.developers.mytodoapp;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */

public class MainFragment extends Fragment {

    View view;
    LayoutInflater inflater;
    TextView tvNoTask;
    RecyclerView rvTaskList;
    ArrayList<Task> taskArrayList = new ArrayList<Task>();
    TaskAdapter taskAdapter;
    RelativeLayout rlAddTaskContainer;

    //  newTask popup window layout objects
    EditText etNewTaskName;
    TextView tvReminder;
    Switch switchReminder;
    int mHour = 0, mMinute;
    long reminderTimeInMillis;
    RelativeLayout rlAddReminder;

    //  SQL
    SqLiteTaskHelper taskHelper;
    SQLiteDatabase db;

    public MainFragment() {
//         Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        view = inflater.inflate(R.layout.fragment_main, container, false);
        taskAdapter =  new TaskAdapter(taskArrayList,getContext(),view);
//      Method to check weather to Display A Message (or not) on the home screen if no active task available
        noTaskMsgToggle(view);

        rlAddTaskContainer = (RelativeLayout)view.findViewById(R.id.rlAddTaskContainer);
        rlAddTaskContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlAddTaskContainer.setVisibility(View.INVISIBLE);
                newTaskAlertBox();
            }
        });

        rvTaskList = (RecyclerView) view.findViewById(R.id.rvTaskList);
        rvTaskList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTaskList.setItemAnimator(new DefaultItemAnimator());
        rvTaskList.setAdapter(taskAdapter);
        prepareTask(getContext());

        // Swipe To Refresh
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.srlMainFragment);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                refreshTaskList(getContext());
                noTaskMsgToggle(view);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return view;
    }

    public void prepareTask(Context context) {

//      Clearing the Array List
        taskArrayList.clear();
        taskHelper = SqLiteTaskHelper.getInstance(context);
        db = taskHelper.getReadableDatabase();
        String Projection[] = {"TASK_NAME", "TASK_STATUS", "TASK_ALARM"};
        Cursor cursor = db.query("TASK_LIST", Projection, null, null, null, null, null);

//      Repopulating it
        while (cursor.moveToNext()) {
            String status_check = cursor.getString(1);
            if (status_check.equals("0")) {
                Task task = new Task(cursor.getString(0));
                taskArrayList.add(task);
            }
        }
        cursor.close();
        taskHelper.close();
        db.close();
        taskAdapter.notifyDataSetChanged();
    }

    public void refreshTaskList(Context context){

//      un-setting and resetting the adapter so the view is redrawn without old style
        rvTaskList.setAdapter(null);
        rvTaskList.setAdapter(taskAdapter);
        prepareTask(context);
    }

//    Check for the number of active tasks in db, if 0 then display msg if more then 0 hide msg
    public void noTaskMsgToggle(View view){
        tvNoTask = (TextView)view.findViewById(R.id.tvNoTask);
        int count = SqLiteTaskHelper.getNoOfPendingTask(getContext());
        if(count==0){
            String displayString = "What do you want to do today?";
            String temp = "";
            tvNoTask.setVisibility(TextView.VISIBLE);
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if(hour < 12 && hour >= 4) {
                temp = "Good Morning,\n";
            } else if (hour >=12 && hour < 16){
                temp = "Good Afternoon,\n";
            } else {
                temp = "Good Evening,\n";
            }
            displayString = temp+displayString;
            tvNoTask.setText(displayString);
        }else{
            tvNoTask.setVisibility(TextView.INVISIBLE);
        }
    }

    public void pickTime(){
        Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
                Calendar setCal = Calendar.getInstance();
                setCal.set(Calendar.HOUR_OF_DAY,mHour);
                setCal.set(Calendar.MINUTE,mMinute);
                reminderTimeInMillis = setCal.getTimeInMillis();

                long currentTimeinMillis = Calendar.getInstance().getTimeInMillis();

                if(reminderTimeInMillis - currentTimeinMillis < 0){
                    Toast.makeText(getContext(), "The time set for reminder has already passed", Toast.LENGTH_LONG).show();
                    reminderTimeInMillis = 0;
                    mHour = 0;
                    mMinute = 0;
                    tvReminder.setText("");
                    switchReminder.setChecked(false);
                }else{
                    Task temp = new Task("Temp");
                    temp.setAlarmTime(hourOfDay, minute);
                    tvReminder.setText("Reminder Set at "+temp.getAlarmTime());
                    switchReminder.setChecked(true);
                    mHour = hourOfDay;
                    mMinute = minute;
                }
            }
        }, mHour, mMinute, false);
        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                switchReminder.setChecked(false);
            }
        });
        timePickerDialog.show();
    }

    public void newTaskAlertBox(){
        View alertBox = inflater.inflate(R.layout.newtask, null);
        etNewTaskName = (EditText) alertBox.findViewById(R.id.etNewTaskName);
        tvReminder = (TextView)alertBox.findViewById(R.id.tvReminder);
        switchReminder =(Switch)alertBox.findViewById(R.id.switchReminder);
        rlAddReminder = (RelativeLayout)alertBox.findViewById(R.id.rlAddReminder);

        switchReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    pickTime();
                    rlAddReminder.setVisibility(View.VISIBLE);
                }else {
                    rlAddReminder.setVisibility(View.INVISIBLE);
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("New Task");
        builder.setView(alertBox);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rlAddTaskContainer.setVisibility(View.VISIBLE);
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                rlAddTaskContainer.setVisibility(View.VISIBLE);
            }
        });

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Task newTask = new Task(etNewTaskName.getText().toString().trim());
                newTask.setAlarmTime(mHour,mMinute);
                final String Task_Id = null;
                final String TaskName = newTask.getTaskName();
                final String Task_Status = newTask.TaskStatus;
                final String Task_Alarm = newTask.getAlarmTime();
                final int Task_Alarm_Request_Code;

                if(TaskName.equals("")){
                    Toast.makeText(getContext(),"Task name cannot be empty",Toast.LENGTH_SHORT).show();
                }else{

//                  Setting the alarm
                    if(switchReminder.isChecked()){
                        Task_Alarm_Request_Code = (int)Calendar.getInstance().getTimeInMillis();
                        MyAlarmManager myAlarmManager = new MyAlarmManager(getContext());
                        myAlarmManager.setReminder(reminderTimeInMillis, Task_Alarm_Request_Code, TaskName);
                        switchReminder.setChecked(false);
                    }else{
                        Task_Alarm_Request_Code = 0;
                    }

//                  Inserting into Db
                    taskHelper = SqLiteTaskHelper.getInstance(getContext());
                    db = taskHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("TASK_ID", Task_Id);
                    values.put("TASK_NAME", TaskName);
                    values.put("TASK_STATUS", Task_Status);
                    values.put("TASK_ALARM", Task_Alarm);
                    values.put("TASK_ALARM_REQUEST_CODE", Task_Alarm_Request_Code);
                    long row = db.insert("TASK_LIST", null, values);
                    db.close();
                    taskHelper.close();
                    Toast.makeText(getContext(),"Task Added", Toast.LENGTH_SHORT).show();

//                  Method to check weather to Display A Message (or not) on the home screen if no active task available
                    noTaskMsgToggle(view);
                    refreshTaskList(getContext());
                }
                rlAddTaskContainer.setVisibility(View.VISIBLE);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimationUpDown;
        dialog.show();
    }
}



