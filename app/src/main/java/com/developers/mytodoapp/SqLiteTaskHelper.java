package com.developers.mytodoapp;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dom on 14/8/17.
 */

public class SqLiteTaskHelper extends SQLiteOpenHelper {
    private static SqLiteTaskHelper mInstance = null;
    public String TABLE_TASK_INSIGHT = "TASK_INSIGHT";
    public String KEY_ID = "TASK_INSIGHT_ID";
    public String KEY_NAME = "TASK_INSIGHT_NAME";
    public String KEY_STATUS = "TASK_INSIGHT_STATUS";
    private String CREATE_TABLE_TASK_INSIGHT = "CREATE TABLE "+TABLE_TASK_INSIGHT+" (TASK_INSIGHT_ID INTEGER PRIMARY KEY AUTOINCREMENT, TASK_INSIGHT_NAME TEXT, TASK_INSIGHT_STATUS INT(1))";

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
        db.execSQL("CREATE TABLE TASK_LIST (TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT, TASK_NAME TEXT, TASK_TAGS TEXT, TASK_STATUS INT(1))");
        db.execSQL(CREATE_TABLE_TASK_INSIGHT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS TASK_LIST");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TASK_INSIGHT);
        onCreate(db);
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


}
