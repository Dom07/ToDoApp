package com.developers.mytodoapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static android.view.View.inflate;

/**
 * Created by dom on 2/8/17.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    ArrayList<Task> taskArrayList;
    Context context;



    public TaskAdapter(ArrayList<Task> taskArrayList,Context context) {
        this.taskArrayList = taskArrayList;
        this.context = context;
    }

    @Override
    public TaskAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taskitem,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TaskAdapter.MyViewHolder holder, int position) {
        Task task = taskArrayList.get(position);
        final String TaskName = task.getTaskName().toString();
        final SpannableString SSTaskName = SpannableString.valueOf(TaskName);
        SSTaskName.setSpan(new StrikethroughSpan(),0,SSTaskName.length(),0);
        holder.tvTaskName.setText(task.getTaskName());
        holder.ivTaskMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.ivTaskMenuButton);
                popupMenu.inflate(R.menu.task_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.TaskMenuItemDelete:
                                SqLiteTaskHelper.delItem(context,TaskName);
                                holder.tvTaskName.setText(SSTaskName);
                                holder.ivTaskMenuButton.setVisibility(View.INVISIBLE);
                                Toast.makeText(context,"Task Deleted, Swipe to Refresh",Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskName;
        ImageView ivTaskMenuButton;

        public MyViewHolder(View view) {
            super(view);
            tvTaskName = (CheckBox) view.findViewById(R.id.tvTaskName);
            ((CheckBox)view.findViewById(R.id.tvTaskName)).setChecked(false);
            ivTaskMenuButton = (ImageView)view.findViewById(R.id.ivTaskMenuButton);
        }
    }
}
