package com.developers.mytodoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by dom on 21/9/17.
 */

public class ClearDbReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SqLiteTaskHelper sqLiteTaskHelper = SqLiteTaskHelper.getInstance(context);
        sqLiteTaskHelper.clearDb(context);
        sqLiteTaskHelper.close();
        Log.i("DB Clear","True");
    }
}
