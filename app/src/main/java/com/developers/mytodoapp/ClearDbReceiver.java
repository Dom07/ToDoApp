package com.developers.mytodoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by dom on 21/9/17.
 */

public class ClearDbReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SqLiteTaskHelper sqLiteTaskHelper = SqLiteTaskHelper.getInstance(context);
//        Log.d("DB","Executing DB Clear method");
        sqLiteTaskHelper.clearDb(context);
    }
}
