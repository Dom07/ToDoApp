package com.developers.mytodoapp;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    RecyclerView rvTaskList;
    TaskAdapter taskAdapter;
    FloatingActionButton fabAddTask;
    ArrayList<Task> taskArrayList = new ArrayList<Task>();

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main,container,false);

        //Floating Action Button
        fabAddTask = (FloatingActionButton)view.findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("New Task")
                        .setView(R.layout.newtask)
                        .setPositiveButton("ok",null)
                        .setNegativeButton("cancel",null)
                        .create().show();
            }
        });


        rvTaskList = (RecyclerView)view.findViewById(R.id.rvTaskList);
        taskAdapter = new TaskAdapter(taskArrayList);
        rvTaskList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTaskList.setItemAnimator(new DefaultItemAnimator());
        rvTaskList.setAdapter(taskAdapter);
        prepareTask();
        return view;
    }

    private void prepareTask(){
        Task task = new Task("Task1","personal");
        taskArrayList.add(task);
        task = new Task("Task2","urgent");
        taskArrayList.add(task);
        task = new Task("Task3","nextweek");
        taskArrayList.add(task);
        taskAdapter.notifyDataSetChanged();
    }
}

