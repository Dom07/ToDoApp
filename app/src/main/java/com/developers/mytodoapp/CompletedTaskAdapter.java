package com.developers.mytodoapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by dom on 8/9/17.
 */

public class CompletedTaskAdapter extends RecyclerView.Adapter<CompletedTaskAdapter.MyViewHolder>{

    ArrayList<Task> TaskList;

    public CompletedTaskAdapter(ArrayList<Task> TaskList) {
        this.TaskList = TaskList;
    }

    @Override
    public CompletedTaskAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completedtaskitem,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Task task = TaskList.get(position);
        holder.tvCompletedTaskName.setText(task.getTaskName());
    }

    @Override
    public int getItemCount() {
        return TaskList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCompletedTaskName;

        public MyViewHolder(View view) {
            super(view);
            tvCompletedTaskName = (TextView)view.findViewById(R.id.tvCompletedTaskName);
        }
    }
}
