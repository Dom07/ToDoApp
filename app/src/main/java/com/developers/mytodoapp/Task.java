package com.developers.mytodoapp;

import android.nfc.Tag;

/**
 * Created by dom on 1/8/17.
 */

public class Task {
    String TaskName;
    String AlarmTime;
    String TaskStatus = "0";

    public Task(String TaskName){
        this.setTaskName(TaskName);
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public void setAlarmTime(int Hour, int Minute){
        if(Hour == 25){
            AlarmTime = "0";
        }else{
            if(Hour > 12){
                Hour = Hour - 12;
                if(Minute <10) {
                    AlarmTime = Hour + ": 0"+ Minute + " PM";
                }else {
                    AlarmTime = Hour + ": "+ Minute + " PM";
                }
            }else{
                if(Minute <10) {
                    AlarmTime = Hour + ": 0"+ Minute + " AM";
                }else {
                    AlarmTime = Hour + ": "+ Minute + " AM";
                }
            }
        }

    }

    public String getAlarmTime(){
        return AlarmTime;
    }

}
