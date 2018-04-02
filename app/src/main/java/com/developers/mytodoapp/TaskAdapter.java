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
import android.widget.RelativeLayout;
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

    int mHour=0, mMinute=0;

    ArrayList<Task> taskArrayList;

    Context context;

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
        final int alarmRequestCode = SqLiteTaskHelper.getAlarmRequestCode(context, TaskName);

        if(alarmRequestCode != 0){
            final String reminderTime = SqLiteTaskHelper.getAlarmTime(context, TaskName);
            holder.ivAlarmStatus.setImageResource(R.drawable.ic_alarm_on);
            holder.tvAlarmTime.setText(reminderTime);
        }else{
            holder.ivAlarmStatus.setImageResource(R.drawable.ic_add_alarm);
            holder.tvAlarmTime.setText("");
        }

        holder.tvTaskName.setText(task.getTaskName());
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
                    holder.ivAlarmStatus.setVisibility(View.INVISIBLE);
                    holder.tvAlarmTime.setVisibility(View.INVISIBLE);
                } else {
                    SqLiteTaskHelper.markTaskAsNotComplete(context, TaskName);
                    holder.ivTaskDeleteMain.setVisibility(View.VISIBLE);
                    holder.ivAlarmStatus.setVisibility(View.VISIBLE);
                    holder.tvAlarmTime.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.rlAlarmStatusContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int refetchedAlarmRequestCode = SqLiteTaskHelper.getAlarmRequestCode(context, TaskName);
                if(refetchedAlarmRequestCode !=0){
                    MyAlarmManager myAlarmManager = new MyAlarmManager(context);
                    myAlarmManager.cancelReminder(alarmRequestCode, TaskName);
                    holder.ivAlarmStatus.setImageResource(R.drawable.ic_add_alarm);
                    holder.tvAlarmTime.setText("");
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

                                holder.ivAlarmStatus.setImageResource(R.drawable.ic_alarm_on);
                                holder.tvAlarmTime.setText(SqLiteTaskHelper.getAlarmTime(context,TaskName));

                            }else{
                                Toast.makeText(context,"Time you selected has already passed",Toast.LENGTH_SHORT).show();
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
        CheckBox checkBox;
        ImageView ivTaskDeleteMain;
        ImageView ivAlarmStatus;
        TextView tvAlarmTime;
        RelativeLayout rlAlarmStatusContainer;

        public MyViewHolder(View view) {
            super(view);
            tvTaskName = (CheckBox) view.findViewById(R.id.tvTaskName);
            checkBox = (CheckBox)view.findViewById(R.id.tvTaskName);
            ((CheckBox)view.findViewById(R.id.tvTaskName)).setChecked(false);
            ivTaskDeleteMain = (ImageView)view.findViewById(R.id.ivTaskDeleteMain);
            ivAlarmStatus = (ImageView)view.findViewById(R.id.ivAlarmStatus);
            tvAlarmTime = (TextView)view.findViewById(R.id.tvAlarmTime);
            rlAlarmStatusContainer = (RelativeLayout)view.findViewById(R.id.rlAlarmStatusContainer);
        }
    }


}
