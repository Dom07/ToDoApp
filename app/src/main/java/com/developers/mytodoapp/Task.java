package com.developers.mytodoapp;

import android.nfc.Tag;

/**
 * Created by dom on 1/8/17.
 */

public class Task {
    String TaskName;
    String Tags;

    public Task(String TaskName,String Tags){
        this.setTaskName(TaskName);
        this.setTags(Tags);
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public String getTags() {
        return Tags;
    }

    public void setTags(String tags) {
        Tags = tags;
    }
}
