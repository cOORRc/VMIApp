package com.KioskApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyServiceManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equalsIgnoreCase("android.intent.action.BOOT_COMPLETED"))
        {
            Intent myIntent=new Intent(context,SplashActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        }
    }
}
