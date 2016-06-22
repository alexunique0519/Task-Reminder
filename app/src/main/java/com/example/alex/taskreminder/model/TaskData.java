package com.example.alex.taskreminder.model;

import android.app.LauncherActivity;

import java.sql.Date;
import java.sql.Time;

/**
 * Created by Alex on 2016-06-18.
 */
//define a taskData model class corresponding to all the columns in database table
public class TaskData {
    public int taskId;
    public String taskName;
    public String dueDate;
    public String dueTime;
    public String description;
}
