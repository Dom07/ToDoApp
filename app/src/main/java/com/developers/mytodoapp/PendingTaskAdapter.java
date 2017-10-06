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
        Task task = PendingTaskList.get(position);
        holder.tvPendingTaskName.setText(task.getTaskName());
        holder.ivMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.ivMenuButton);
                popupMenu.inflate(R.menu.pending_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String TaskName;
                        switch (item.getItemId()){
                            case R.id.MenuItemTaskDelete:
                                TaskName = holder.tvPendingTaskName.getText().toString();
                                SqLiteTaskHelper.delItem(context,TaskName);
                                holder.ivMenuButton.setVisibility(View.INVISIBLE);
                                SpannableString spTaskName = SpannableString.valueOf(TaskName);
                                spTaskName.setSpan(new StrikethroughSpan(),0,spTaskName.length(),0);
                                holder.tvPendingTaskName.setText(spTaskName);
                                Toast.makeText(context,"Task deleted, swipe down to refresh",Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.MenuItemTaskRestore:
                                TaskName = holder.tvPendingTaskName.getText().toString();
                                SqLiteTaskHelper.changeStatus(context,TaskName);
                                holder.ivMenuButton.setVisibility(View.INVISIBLE);
                                holder.tvPendingTaskName.setText(TaskName+" (Restored)");
                                Toast.makeText(context,"Task added to today's task list, swipe down to refresh",Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return PendingTaskList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
         TextView tvPendingTaskName;
         ImageView ivMenuButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvPendingTaskName= (TextView)itemView.findViewById(R.id.tvPendingTaskName);
            ivMenuButton = (ImageView) itemView.findViewById(R.id.ivMenuButton);
        }
    }
}
