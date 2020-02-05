package com.tmsoft.tm.elitrends.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PublicNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent background = new Intent(context, PublicBackgroundService.class);
        context.startService(background);
    }
}
