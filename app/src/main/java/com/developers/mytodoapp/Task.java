package com.developers.mytodoapp;

/**
 * Created by dom on 1/8/17.
 */

public class Task {
    String TaskId = null;
    String TaskName;
    String TaskStatus = "0";
    String AlarmTime;

    public String getTask_Alarm_Request_Code() {
        return Task_Alarm_Request_Code;
    }

    public void setTask_Alarm_Request_Code(String task_Alarm_Request_Code) {
        Task_Alarm_Request_Code = task_Alarm_Request_Code;
    }

    String Task_Alarm_Request_Code;

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
            if(Hour >= 12){
                if(Hour > 12) {
                    Hour = Hour - 12;
                }
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
