package com.developers.mytodoapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by root on 29/9/17.
 */

public class PendingTaskAdapter extends RecyclerView.Adapter<PendingTaskAdapter.MyViewHolder> {

    ArrayList<Task> PendingTaskList;
    Context context;

    public PendingTaskAdapter(ArrayList<Task> PendingTaskList,Context context){
        this.PendingTaskList = PendingTaskList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yesterdayspendingtaskitem,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Task task = PendingTaskList.get(position);
        final SpannableString TaskName = SpannableString.valueOf(task.getTaskName().toString());
        holder.tvPendingTaskName.setText(task.getTaskName());
        holder.ivRestoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SqLiteTaskHelper.changeStatus(context,task.getTaskName());
                TaskName.setSpan(new StrikethroughSpan(),0,TaskName.length(),0);
                holder.tvPendingTaskName.setText(TaskName);
                Toast.makeText(context,"Task Restored",Toast.LENGTH_SHORT).show();
                holder.ivRestoreButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return PendingTaskList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
         TextView tvPendingTaskName;
         ImageView ivRestoreButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvPendingTaskName= (TextView)itemView.findViewById(R.id.tvPendingTaskName);
            ivRestoreButton = (ImageView) itemView.findViewById(R.id.ivRestoreButton);
        }
    }
}
