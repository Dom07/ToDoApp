package com.developers.mytodoapp;

import android.app.DatePickerDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */

public class MainFragment extends Fragment {

    RecyclerView rvTaskList;
    FloatingActionButton fabAddTask;
    CheckBox tvTaskName;
    ArrayList<Task> taskArrayList = new ArrayList<Task>();
    TaskAdapter taskAdapter = new TaskAdapter(taskArrayList);

    //  newTask popup window layout objects
    EditText etNewTag;
    EditText etNewTaskName;
    Spinner spinnerDateView;
    TextView tvCustomDate;
    Button btnTimePicker;
    ArrayAdapter<CharSequence> spinnerAdapter;


    //  Date and Time Picker Dialogue
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyy");

    //  SQL
    SqLiteTaskHelper taskHelper;

    public MainFragment() {
//         Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_main, container, false);

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
                etNewTag = (EditText) alertBox.findViewById(R.id.etNewTags);
                spinnerDateView = (Spinner) alertBox.findViewById(R.id.spinnerDateView);
                tvCustomDate = (TextView) alertBox.findViewById(R.id.tvCustomDate);
                btnTimePicker = (Button) alertBox.findViewById(R.id.btnTimePicker);
                spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.DaySelector, android.R.layout.simple_spinner_item);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDateView.setAdapter(spinnerAdapter);

                spinnerDateView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if (parent.getSelectedItemPosition() == 1) {

//                          Formatting The Text View With Today's Date
                            Calendar calendar = Calendar.getInstance();
                            String currentDate = df.format(calendar.getTime());
                            tvCustomDate.setText(currentDate);

                        } else if (parent.getSelectedItemPosition() == 2) {

//                          Formatting the text view with tomorrow's date
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                            String tommorrowsDate = df.format(calendar.getTime());
                            tvCustomDate.setText(tommorrowsDate);

                        } else if (parent.getSelectedItemPosition() == 3) {

//                          Creating a datepicker to get custom date and setting the textview with it
                            DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    calendar.set(Calendar.YEAR, year);
                                    calendar.set(Calendar.MONTH, month);
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    String newDate = df.format(calendar.getTime());
                                    tvCustomDate.setText(newDate);
                                }
                            };
                            new DatePickerDialog(getContext(), date,
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                btnTimePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);


                        TimePickerDialog timePickerDialog;
                        timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                btnTimePicker.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, false);
                        timePickerDialog.setTitle("Select Time");
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
                        taskHelper = new SqLiteTaskHelper(getContext());
                        SQLiteDatabase db = taskHelper.getWritableDatabase();

                        final String TaskName = etNewTaskName.getText().toString().trim();
                        final String Tags = etNewTag.getText().toString().trim();
                        final String Date = tvCustomDate.getText().toString().trim();
                        final String Time = btnTimePicker.getText().toString().trim();
                        final String Task_Id = null;
                        final String Task_Status = "0";
                        ContentValues values = new ContentValues();
                        values.put("TASK_ID", Task_Id);
                        values.put("TASK_NAME", TaskName);
                        values.put("TASK_TAGS", Tags);
                        values.put("TASK_END_DATE", Date);
                        values.put("TASK_END_TIME", Time);
                        values.put("TASK_STATUS", Task_Status);
                        long row = db.insert("TASK_LIST", null, values);
                        Toast.makeText(getContext(), "row number is:" + row, Toast.LENGTH_SHORT).show();
                        prepareTask(getContext());
                    }
                });
                AlertDialog dialog = builder.create();
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
                refreshTaskList(getContext());
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
        SqLiteTaskHelper taskHelper = new SqLiteTaskHelper(context);
        SQLiteDatabase db = taskHelper.getReadableDatabase();
        String Projection[] = {"TASK_NAME", "TASK_TAGS", "TASK_STATUS"};
        Cursor cursor = db.query("TASK_LIST", Projection, null, null, null, null, null);

//      Repopulating it
        while (cursor.moveToNext()) {
            String status_check = cursor.getString(2);
            if (status_check.equals("0")) {
                Task task = new Task(cursor.getString(0), cursor.getString(1));
                taskArrayList.add(task);
            }
        }
        cursor.close();
        taskAdapter.notifyDataSetChanged();
    }

    public void refreshTaskList(Context context){

//      un-setting and resetting the adapter so the view is redrawn without old style
        rvTaskList.setAdapter(null);
        rvTaskList.setAdapter(taskAdapter);
        prepareTask(context);
    }
}



