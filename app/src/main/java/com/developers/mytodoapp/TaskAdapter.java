package com.developers.mytodoapp;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.SQLClientInfoException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.Inflater;

import static android.view.View.inflate;

/**
 * Created by dom on 2/8/17.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder>  {

    ArrayList<Task> taskArrayList;

    Context context;

    Calendar calendar = Calendar.getInstance();

    int Hour, Minute, AlarmFlag=0;

    View fragmentView;

    public TaskAdapter(ArrayList<Task> taskArrayList,Context context,View fragmentView) {
        this.taskArrayList = taskArrayList;
        this.context = context;
        this.fragmentView = fragmentView;
    }

    @Override
    public TaskAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taskitem,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TaskAdapter.MyViewHolder holder, final int position) {
        Task task = taskArrayList.get(position);
        final String TaskName = task.getTaskName().toString();
        final String AlarmTime = SqLiteTaskHelper.getAlarmTime(context, TaskName);
        holder.tvTaskName.setText(task.getTaskName());
        holder.tvAlarmTime.setText(AlarmTime);
        holder.ivTaskDeleteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SqLiteTaskHelper.delItem(context, TaskName);
                taskArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,taskArrayList.size());
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SqLiteTaskHelper.markTaskAsComplete(context, TaskName);
                    holder.ivTaskDeleteMain.setVisibility(View.INVISIBLE);
                } else {
                    SqLiteTaskHelper.markTaskAsNotComplete(context, TaskName);
                    holder.ivTaskDeleteMain.setVisibility(View.VISIBLE);
                }
            }
        });

//        holder.ivAlarmAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(AlarmFlag==0){
//                    AlarmFlag =1;
//                    holder.ivAlarmOn.setVisibility(View.VISIBLE);
//                    holder.ivAlarmAdd.setVisibility(View.INVISIBLE);
//                    holder.tvAlarmTime.setText("Reminder Set : 10:00 AM");
//                }
//            }
//        });
//
//        holder.ivAlarmOn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(AlarmFlag==1){
//                    AlarmFlag=0;
//                    holder.tvAlarmTime.setText("");
//                    holder.ivAlarmAdd.setVisibility(View.VISIBLE);
//                    holder.ivAlarmOn.setVisibility(View.INVISIBLE);
//                }
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskName;
        CheckBox checkBox;
        ImageView ivTaskDeleteMain;
        ImageView ivAlarmStatus;
        TextView tvAlarmTime;

        public MyViewHolder(View view) {
            super(view);
            tvTaskName = (CheckBox) view.findViewById(R.id.tvTaskName);
            checkBox = (CheckBox)view.findViewById(R.id.tvTaskName);
            ((CheckBox)view.findViewById(R.id.tvTaskName)).setChecked(false);
            ivTaskDeleteMain = (ImageView)view.findViewById(R.id.ivTaskDeleteMain);
//            ivAlarmStatus = (ImageView)view.findViewById(R.id.ivAlarmAdd);
            tvAlarmTime = (TextView)view.findViewById(R.id.tvAlarmTime);
        }
    }


}
