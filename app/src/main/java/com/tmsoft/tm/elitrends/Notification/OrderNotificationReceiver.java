package com.tmsoft.tm.elitrends.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OrderNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent background = new Intent(context, OrderBackgroundService.class);
        context.startService(background);
    }
}
