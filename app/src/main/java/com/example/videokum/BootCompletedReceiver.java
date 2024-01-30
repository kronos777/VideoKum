package com.example.videokum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootCompletedReceiver extends BroadcastReceiver {

    private final String your_message = "Reciver work";

    public BootCompletedReceiver() {
    }

    @Override
     public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            //Intent intent = new Intent(this, MainActivity.class);
            //context.startActivity(MainActivity.class);
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
                Intent i = new Intent(context, SplashActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }

    public String getYour_message() {
        return your_message;
    }
}
