package com.developers.mytodoapp;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */

public class MainFragment extends Fragment {

    TextView tvNoTask;
    Spinner spAMPM;
    RecyclerView rvTaskList;
    FloatingActionButton fabAddTask;
    CheckBox tvTaskName;
    ArrayList<Task> taskArrayList = new ArrayList<Task>();
    TaskAdapter taskAdapter;

    //  newTask popup window layout objects
    EditText etNewTaskName;
    TextView tvReminder;
    Switch switchReminder;
    int mHour, mMinute;


    //  SQL
    SqLiteTaskHelper taskHelper;

    public MainFragment() {
//         Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        taskAdapter =  new TaskAdapter(taskArrayList,getContext(),view);
//      Method to check weather to Display A Message (or not) on the home screen if no active task available
        noTaskMsgToggle(view);

//      Check Box
        tvTaskName = (CheckBox) view.findViewById(R.id.tvTaskName);

//      Floating Action Button
        fabAddTask = (FloatingActionButton) view.findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//              New Task AlertBox code
                View alertBox = inflater.inflate(R.layout.newtask, null);
                etNewTaskName = (EditText) alertBox.findViewById(R.id.etNewTaskName);
                tvReminder = (TextView)alertBox.findViewById(R.id.tvReminder);
                switchReminder =(Switch)alertBox.findViewById(R.id.switchReminder);

                tvReminder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinute = c.get(Calendar.MINUTE);
                        TimePickerDialog  timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mHour = hourOfDay;
                                mMinute = minute;
                                tvReminder.setText("Alarm Set:"+hourOfDay+":"+minute);
                                switchReminder.setChecked(true);
                            }
                        },mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("New Task");
                builder.setView(alertBox);
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        Inserting Values into database
                        taskHelper = SqLiteTaskHelper.getInstance(getContext());
                        SQLiteDatabase db = taskHelper.getWritableDatabase();


                        Task newTask = new Task(etNewTaskName.getText().toString().trim());
                        newTask.setAlarmTime(mHour,mMinute);
                        final String Task_Id = null;
                        final String TaskName = newTask.getTaskName();
                        final String Task_Status = newTask.TaskStatus;
                        final String Task_Alarm = newTask.getAlarmTime();
                        if(TaskName.equals("")){
                            Toast.makeText(getContext(),"Task name cannot be empty",Toast.LENGTH_SHORT).show();
                        }else{
                            ContentValues values = new ContentValues();
                            values.put("TASK_ID", Task_Id);
                            values.put("TASK_NAME", TaskName);
                            values.put("TASK_STATUS", Task_Status);
                            values.put("TASK_ALARM", Task_Alarm);
                            long row = db.insert("TASK_LIST", null, values);
                            Log.d("TASK_LIST","Task Name: "+TaskName+" Task_Statues: "+Task_Status+" Task_Alarm: "+Task_Alarm);
                            Toast.makeText(getContext(),"Task Added", Toast.LENGTH_SHORT).show();
                            db.close();
                            taskHelper.close();
    //                      Method to check weather to Display A Message (or not) on the home screen if no active task available
                            noTaskMsgToggle(view);
                            refreshTaskList(getContext());
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimationUpDown;
                dialog.show();
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
        SqLiteTaskHelper taskHelper = SqLiteTaskHelper.getInstance(context);
        SQLiteDatabase db = taskHelper.getReadableDatabase();
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
            tvNoTask.setVisibility(TextView.VISIBLE);
        }else{
            tvNoTask.setVisibility(TextView.INVISIBLE);
        }
    }
}



