package com.developers.mytodoapp;

import android.nfc.Tag;

/**
 * Created by dom on 1/8/17.
 */

public class Task {
    String TaskName;

    public Task(String TaskName){
        this.setTaskName(TaskName);
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }
}
