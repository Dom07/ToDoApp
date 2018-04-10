package com.developers.mytodoapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    View fragmentView;

    public PendingTaskAdapter(ArrayList<Task> PendingTaskList,Context context, View fragmentView){
        this.PendingTaskList = PendingTaskList;
        this.context = context;
        this.fragmentView = fragmentView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yesterdayspendingtaskitem,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Task task = PendingTaskList.get(position);
        holder.tvPendingTaskName.setText(task.getTaskName());

        holder.ivRestoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SqLiteTaskHelper.restoreTask(context,task.getTaskName());
                PendingTaskList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,PendingTaskList.size());
                Toast.makeText(context,"Task Added To Today's List",Toast.LENGTH_SHORT).show();
                YesterdaysPendingFragment fragment = new YesterdaysPendingFragment();
                fragment.noDataMsgToggle(fragmentView);
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
