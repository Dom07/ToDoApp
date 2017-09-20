package com.developers.mytodoapp;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompTaskFragment extends Fragment {

    RecyclerView rvCompletedTaskList;
    ArrayList<Task> completedTaskList = new ArrayList<Task>();
    CompletedTaskAdapter completedTaskAdapter = new CompletedTaskAdapter(completedTaskList);
    SwipeRefreshLayout swipeRefreshLayout;


    public CompTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_comp_task,container,false);
        rvCompletedTaskList = (RecyclerView)view.findViewById(R.id.rvCompletedTaskList);
        rvCompletedTaskList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCompletedTaskList.setItemAnimator(new DefaultItemAnimator());
        rvCompletedTaskList.setAdapter(completedTaskAdapter);
        prepareCompletedTask(getContext());

//        Swipe To Refresh
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.srlCompTaskFragment);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareCompletedTask(getContext());
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return view;
    }

    public void prepareCompletedTask(Context context){
        completedTaskList.clear();
        SqLiteTaskHelper taskHelper = SqLiteTaskHelper.getInstance(context);
        SQLiteDatabase db = taskHelper.getReadableDatabase();
        String Projection[]={"TASK_NAME","TASK_TAGS","TASK_STATUS"};
        Cursor cursor = db.query("TASK_LIST",Projection,null,null,null,null,null);
        while(cursor.moveToNext()) {
            String status_check = cursor.getString(2);
            if (status_check.equals("1")) {
                Task task = new Task(cursor.getString(0), cursor.getString(1));
                completedTaskList.add(task);
            }
        }
        cursor.close();
        db.close();
        taskHelper.close();
        completedTaskAdapter.notifyDataSetChanged();
    }
}
