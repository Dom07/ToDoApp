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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.jar.Manifest;


/**
 * A simple {@link Fragment} subclass.
 */
public class YesterdaysPendingFragment extends Fragment {

    RecyclerView rvYesterdaysPendingTask;
    ArrayList<Task> pendingTaskList = new ArrayList<Task>();
    PendingTaskAdapter pendingTaskAdapter = new PendingTaskAdapter(pendingTaskList,getContext());
    SwipeRefreshLayout srlYesterdaysPendingLayout;
    TextView tvNoPendingTaskYest;

    public YesterdaysPendingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_yesterdayspending, container, false);
        pendingTaskAdapter= new PendingTaskAdapter(pendingTaskList, getContext());
        rvYesterdaysPendingTask = (RecyclerView)view.findViewById(R.id.rvYesterdaysPendingTask);
        srlYesterdaysPendingLayout = (SwipeRefreshLayout)view.findViewById(R.id.srlYesterdaysPendingLayout);
        tvNoPendingTaskYest = (TextView)view.findViewById(R.id.tvNoPendingTaskYest);

        rvYesterdaysPendingTask.setLayoutManager(new LinearLayoutManager(getContext()));
        rvYesterdaysPendingTask.setItemAnimator(new DefaultItemAnimator());
        rvYesterdaysPendingTask.setAdapter(pendingTaskAdapter);
        noDataMsgToggle();
        preparePendingTaskList(getContext());

        srlYesterdaysPendingLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                preparePendingTaskList(getContext());
                noDataMsgToggle();
                srlYesterdaysPendingLayout.setRefreshing(false);
            }
        });

        srlYesterdaysPendingLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return view;
    }

    public void preparePendingTaskList(Context context){
        pendingTaskList.clear();
        SqLiteTaskHelper taskHelper = SqLiteTaskHelper.getInstance(context);
        SQLiteDatabase db = taskHelper.getReadableDatabase();
        String Projection[]={"TASK_NAME","TASK_TAGS","TASK_STATUS"};
        Cursor cursor = db.query("TASK_LIST",Projection,null,null,null,null,null);
        while(cursor.moveToNext()) {
            String status_check = cursor.getString(2);
            if (status_check.equals("2")) {
                Task task = new Task(cursor.getString(0), cursor.getString(1));
                pendingTaskList.add(task);
            }
        }
        cursor.close();
        db.close();
        taskHelper.close();
        pendingTaskAdapter.notifyDataSetChanged();
    }

    private void noDataMsgToggle(){
        int count = SqLiteTaskHelper.getNoOfYesterdaysPendingTask(getContext());
        if(count==0){
            tvNoPendingTaskYest.setVisibility(View.VISIBLE);
        }else{
            tvNoPendingTaskYest.setVisibility(View.INVISIBLE);
        }
    }



}
