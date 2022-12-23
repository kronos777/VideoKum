package com.example.videokum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootCompletedReceiver extends BroadcastReceiver {

    private String your_message = "Reciver work";

    public BootCompletedReceiver() {
    }

    @Override
     public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            //Intent intent = new Intent(this, MainActivity.class);
            //context.startActivity(MainActivity.class);
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
            // ваш код здесь
        }
    }
}
