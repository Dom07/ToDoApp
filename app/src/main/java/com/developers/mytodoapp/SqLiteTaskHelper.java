package com.developers.mytodoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        db.execSQL("CREATE TABLE TASK_LIST (TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT, TASK_NAME TEXT, TASK_TAGS TEXT, TASK_STATUS INT(1))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS TASK_LIST");
        onCreate(db);
    }
}
