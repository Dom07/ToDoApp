package com.developers.mytodoapp;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by dom on 2/8/17.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder>  {

    int mHour=0, mMinute=0;

    ArrayList<Task> taskArrayList;

    Context context;

    View fragmentView;

    MyAlarmManager myAlarmManager;


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
        myAlarmManager = new MyAlarmManager(context);
        final int alarmRequestCode = SqLiteTaskHelper.getAlarmRequestCode(context, TaskName);
        setAlarmViewItems(TaskName,alarmRequestCode, holder);

        holder.tvTaskName.setText(task.getTaskName());

        holder.ivTaskDeleteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SqLiteTaskHelper.delItem(context, TaskName);
                myAlarmManager.cancelReminder(alarmRequestCode,TaskName);
                taskArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,taskArrayList.size());
                MainFragment mainFragment = new MainFragment();
                mainFragment.noTaskMsgToggle(fragmentView);
            }
        });

        holder.ivTaskStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checked.equals(false)){
                    holder.ivTaskStatus.setImageResource(R.drawable.ic_check_circle_checked);
                    holder.checked = true;
                    SqLiteTaskHelper.markTaskAsComplete(context, TaskName);
                    int tempRequestCode = SqLiteTaskHelper.getAlarmRequestCode(context,TaskName);
                    if(tempRequestCode!=0){
                        myAlarmManager.cancelReminder(alarmRequestCode,TaskName);
                        SqLiteTaskHelper.updateAlarmRequestCode(context, TaskName, 0);
                    }
                    holder.ivTaskDeleteMain.setVisibility(View.INVISIBLE);
                    holder.llAlarmTimeContainer.setVisibility(View.INVISIBLE);
                    holder.ivAlarmStatus.setVisibility(View.INVISIBLE);
                }else{
                    holder.ivTaskStatus.setImageResource(R.drawable.ic_check_circle_unchecked);
                    holder.checked = false;
                    SqLiteTaskHelper.markTaskAsNotComplete(context, TaskName);
                    holder.ivTaskDeleteMain.setVisibility(View.VISIBLE);
                    holder.llAlarmTimeContainer.setVisibility(View.VISIBLE);
                    holder.ivAlarmStatus.setVisibility(View.VISIBLE);
                }
                setAlarmViewItems(TaskName, SqLiteTaskHelper.getAlarmRequestCode(context,TaskName),holder);
            }
        });

        holder.ivAlarmStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int refetchedAlarmRequestCode = SqLiteTaskHelper.getAlarmRequestCode(context, TaskName);
                if(refetchedAlarmRequestCode !=0){
                    myAlarmManager.cancelReminder(alarmRequestCode, TaskName);
                    holder.ivAlarmStatus.setImageResource(R.drawable.ic_add_alarm);
                    holder.tvAlarmTime.setText("");
                    holder.llAlarmTimeContainer.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "Alarm cancelled for "+TaskName,Toast.LENGTH_SHORT).show();
                }else{
                    Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mHour = hourOfDay;
                            mMinute = minute;
                            Calendar newCal = Calendar.getInstance();
                            newCal.set(Calendar.HOUR_OF_DAY, mHour);
                            newCal.set(Calendar.MINUTE, mMinute);
                            long currentTimeinMillis = Calendar.getInstance().getTimeInMillis();
                            if(newCal.getTimeInMillis() - currentTimeinMillis >= 0){
                                int RequestCode = (int) Calendar.getInstance().getTimeInMillis();
                                long AlarmTimeinMillis = newCal.getTimeInMillis();

                                SqLiteTaskHelper.updateAlarmRequestCode(context,TaskName, RequestCode);
                                SqLiteTaskHelper.updateAlarmTime(context,TaskName,mHour,mMinute);

                                MyAlarmManager myAlarmManager = new MyAlarmManager(context);
                                myAlarmManager.setReminder(AlarmTimeinMillis,RequestCode,TaskName);

                                holder.ivAlarmStatus.setImageResource(R.drawable.ic_cancel_alarm);
                                holder.tvAlarmTime.setText(SqLiteTaskHelper.getAlarmTime(context,TaskName));
                                holder.llAlarmTimeContainer.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(context,"The time you selected has already passed, please try again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    },mHour, mMinute, false);
                    timePickerDialog.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskName;
        ImageView ivTaskDeleteMain;
        ImageView ivAlarmStatus;
        TextView tvAlarmTime;
        LinearLayout llAlarmTimeContainer;
        ImageView ivTaskStatus;
        Boolean checked = false;

        public MyViewHolder(View view) {
            super(view);
            tvTaskName = (TextView) view.findViewById(R.id.tvTaskName);
            ivTaskDeleteMain = (ImageView)view.findViewById(R.id.ivTaskDeleteMain);
            ivAlarmStatus = (ImageView)view.findViewById(R.id.ivAlarmStatus);
            tvAlarmTime = (TextView)view.findViewById(R.id.tvAlarmTime);
            llAlarmTimeContainer = (LinearLayout)view.findViewById(R.id.llAlarmTimeContainer);
            ivTaskStatus = (ImageView)view.findViewById(R.id.ivTaskStatus);
        }
    }

    private void setAlarmViewItems(String TaskName, int alarmRequestCode, final TaskAdapter.MyViewHolder holder){
        if(alarmRequestCode != 0){
            final String reminderTime = SqLiteTaskHelper.getAlarmTime(context, TaskName);
            holder.ivAlarmStatus.setImageResource(R.drawable.ic_cancel_alarm);
            holder.tvAlarmTime.setText(reminderTime);
            holder.llAlarmTimeContainer.setVisibility(View.VISIBLE);
        }else{
            holder.ivAlarmStatus.setImageResource(R.drawable.ic_add_alarm);
            holder.llAlarmTimeContainer.setVisibility(View.INVISIBLE);
        }
    }
}
