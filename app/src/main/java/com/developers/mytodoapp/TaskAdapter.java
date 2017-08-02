package com.developers.mytodoapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dom on 2/8/17.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    ArrayList<Task> taskArrayList;

    public TaskAdapter(ArrayList<Task> taskArrayList) {
        this.taskArrayList = taskArrayList;
    }

    @Override
    public TaskAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taskitem,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskAdapter.MyViewHolder holder, int position) {
        Task task = taskArrayList.get(position);
        holder.cbTaskName.setText(task.getTaskName());
        holder.tvTags.setText(task.getTags());
    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbTaskName;
        TextView tvTags;

        public MyViewHolder(View view) {
            super(view);
            cbTaskName = (CheckBox)view.findViewById(R.id.cbTaskName);
            tvTags = (TextView)view.findViewById(R.id.tvTags);
        }
    }
}
