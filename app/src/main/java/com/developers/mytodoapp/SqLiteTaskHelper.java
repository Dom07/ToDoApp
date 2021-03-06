package com.developers.mytodoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.widget.Toast;

/**
 * Created by dom on 14/8/17.
 */

public class SqLiteTaskHelper extends SQLiteOpenHelper {
    private static SqLiteTaskHelper mInstance = null;

    public SqLiteTaskHelper(Context context) {
        super(context, "tasks.db", null, 1);
    }

    public static SqLiteTaskHelper getInstance(Context context){
        if(mInstance == null){
            mInstance = new SqLiteTaskHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE TASK_LIST (TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT, TASK_NAME TEXT, TASK_STATUS INT(1), TASK_ALARM TEXT, TASK_ALARM_REQUEST_CODE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS TASK_LIST");
        onCreate(db);
    }

    public static void insertTask(Context context, Task task){
        SqLiteTaskHelper helper = SqLiteTaskHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TASK_ID", task.TaskId);
        values.put("TASK_NAME", task.getTaskName());
        values.put("TASK_STATUS", task.TaskStatus);
        values.put("TASK_ALARM", task.getAlarmTime());
        values.put("TASK_ALARM_REQUEST_CODE", task.getTask_Alarm_Request_Code());
        long row = db.insert("TASK_LIST", null, values);
        db.close();
        helper.close();
    }

    public static int getNoOfCompletedTask(Context context){
        SqLiteTaskHelper sqLiteTaskHelper = SqLiteTaskHelper.getInstance(context);
        SQLiteDatabase db = sqLiteTaskHelper.getReadableDatabase();
        String Projection[]={"TASK_STATUS"};
        Cursor cursor = db.query("TASK_LIST", Projection,"TASK_STATUS='"+1+"'",null,null,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static int getNoOfPendingTask(Context context){
        SqLiteTaskHelper sqLiteTaskHelper = SqLiteTaskHelper.getInstance(context);
        SQLiteDatabase db = sqLiteTaskHelper.getReadableDatabase();
        String Projection[]={"TASK_STATUS"};
        Cursor cursor = db.query("TASK_LIST", Projection,"TASK_STATUS='"+0+"'",null,null,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static int getNoOfYesterdaysPendingTask(Context context){
        SqLiteTaskHelper sqLiteTaskHelper = SqLiteTaskHelper.getInstance(context);
        SQLiteDatabase db = sqLiteTaskHelper.getReadableDatabase();
        String Projection[]={"TASK_STATUS"};
        Cursor cursor = db.query("TASK_LIST", Projection,"TASK_STATUS='"+2+"'",null,null,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static void delItem(Context context,String TaskName){
        SqLiteTaskHelper sqLiteTaskHelper = SqLiteTaskHelper.getInstance(context);
        SQLiteDatabase db = sqLiteTaskHelper.getWritableDatabase();
        db.delete("TASK_LIST","TASK_NAME='"+TaskName+"'",null);
        db.close();
        sqLiteTaskHelper.close();
    }

    public static void restoreTask(Context context, String TaskName){
        SqLiteTaskHelper sqLiteTaskHelper = SqLiteTaskHelper.getInstance(context);
        SQLiteDatabase db = sqLiteTaskHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TASK_STATUS","0");
        db.update("TASK_LIST",values,"TASK_NAME='"+TaskName+"'",null);
        db.close();
        sqLiteTaskHelper.close();
    }

    public void clearDb(Context context){
        SqLiteTaskHelper sqLiteTaskHelper = SqLiteTaskHelper.getInstance(context);
        SQLiteDatabase db = sqLiteTaskHelper.getWritableDatabase();

//        Deleting All Completed Tasks
        int countNoOfCompleteTask = getNoOfCompletedTask(context);
        if(countNoOfCompleteTask!=0){
            db.delete("TASK_LIST","TASK_STATUS='"+1+"'",null);
        }

//        Deleting all abandoned PendingTasks
        int countNoOfYestPendingTask = getNoOfYesterdaysPendingTask(context);
        if (countNoOfYestPendingTask!=0){
            db.delete("TASK_LIST","TASK_STATUS='"+2+"'",null);
        }

//        Changing status of all incomplete tasks(status = 0) to yesterday's pending task(status = 2)
        int countOfPendingTask = getNoOfPendingTask(context);
        if(countOfPendingTask!=0){
            ContentValues values = new ContentValues();
            values.put("TASK_STATUS","2");
            db.update("TASK_LIST",values,"TASK_STATUS='"+0+"'",null);
        }
        db.close();
        sqLiteTaskHelper.close();
    }

    public static void markTaskAsComplete(Context context, String TaskName){
        SqLiteTaskHelper taskHelper = SqLiteTaskHelper.getInstance(context);
        SQLiteDatabase db = taskHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TASK_STATUS", "1");
        db.update("TASK_LIST", values, "TASK_NAME='" + TaskName + "'", null);
        db.close();
        taskHelper.close();
        Toast.makeText(context, "Good Job! ", Toast.LENGTH_SHORT).show();
    }

    public static void markTaskAsNotComplete(Context context, String TaskName){
        SqLiteTaskHelper taskHelper = new SqLiteTaskHelper(context);
        SQLiteDatabase db = taskHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TASK_STATUS", "0");
        db.update("TASK_LIST", values, "TASK_NAME='" + TaskName + "'", null);
        db.close();
        taskHelper.close();
    }

    public static String getAlarmTime(Context context,String TaskName){
        SqLiteTaskHelper taskHelper = new SqLiteTaskHelper(context);
        SQLiteDatabase db = taskHelper.getReadableDatabase();
        String Projection[]={"TASK_ALARM"};
        Cursor cursor = db.query("TASK_LIST", Projection,"TASK_NAME='"+TaskName+"'",null,null,null,null,null);
        cursor.moveToFirst();
        String AlarmTime = cursor.getString(0);
        cursor.close();
        db.close();
        taskHelper.close();
        return AlarmTime;
    }

    public static int getAlarmRequestCode(Context context,String TaskName){
        SqLiteTaskHelper taskHelper = new SqLiteTaskHelper(context);
        SQLiteDatabase db = taskHelper.getReadableDatabase();
        String Projection[]={"TASK_ALARM_REQUEST_CODE"};
        Cursor cursor = db.query("TASK_LIST", Projection,"TASK_NAME='"+TaskName+"'",null,null,null,null,null);
        cursor.moveToFirst();
        String AlarmTime = cursor.getString(0);
        cursor.close();
        db.close();
        taskHelper.close();
        return Integer.parseInt(AlarmTime);
    }

    public static void updateAlarmRequestCode(Context context, String TaskName, int Task_Request_Code){
        SqLiteTaskHelper taskHelper = new SqLiteTaskHelper(context);
        SQLiteDatabase db = taskHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TASK_ALARM_REQUEST_CODE",Task_Request_Code);
        db.update("TASK_LIST", values, "TASK_NAME='"+TaskName+"'",null);
        db.close();
        taskHelper.close();
    }

    public static void updateAlarmTime(Context context, String TaskName, int Hour, int Minute){
        Task temp = new Task("Temp");
        temp.setAlarmTime(Hour,Minute);
        SqLiteTaskHelper taskHelper = new SqLiteTaskHelper(context);
        SQLiteDatabase db = taskHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TASK_ALARM",temp.getAlarmTime());
        db.update("TASK_LIST", values, "TASK_NAME='"+TaskName+"'",null);
        db.close();
        taskHelper.close();
        Toast.makeText(context,"Reminder Set", Toast.LENGTH_SHORT).show();
    }
}
