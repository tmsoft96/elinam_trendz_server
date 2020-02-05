package com.tmsoft.tm.elitrends.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elitrends.Activity.MainActivity;
import com.tmsoft.tm.elitrends.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OrderBackgroundService extends Service {

    private Boolean isRunning;
    private Context context;
    private Thread backgroundThread;

    private NotificationManager notificationManager;
    private int notificationID;
    private RemoteViews remoteViews;
    private int numMessages = 0;

    private int messageCounter = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        @Override
        public void run() {
            //Do all work here
            Log.i("background", "ORDER BACKGROUND SERVICE WORKING");
            getAllBadge();
            stopSelf();
        }
    };

    private void getAllBadge() {
        String getCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.i("id", getCurrentUserId);
        final DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference().child("AllDeviceToken").child(getCurrentUserId);
        tokenRef.keepSynced(true);
        tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("orderDelivery")){
                        String qq = dataSnapshot.child("orderDelivery").getValue().toString();
                        if (qq.equals("no")){
                            if (dataSnapshot.hasChild("order")){
                                String ww = dataSnapshot.child("order").getValue().toString();
                                if (ww.equals("no")){
                                    if (dataSnapshot.hasChild("orderMessage")){
                                        tokenRef.child("orderDelivery").setValue("yes");
                                        String message = dataSnapshot.child("orderMessage").getValue().toString();
                                        notifyUser(message);
                                    }
                                }

                            }
                        }
                    }
                } else
                    Log.i("exit", "no");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void notifyUser(String msg) {
        Log.i("Start", "notification");

        //notification id
        notificationID = (int) System.currentTimeMillis();

        //setting the notification sound
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //customising notification
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_all);

        //setting values
        remoteViews.setImageViewResource(R.id.notificationOrder_icon, R.drawable.reallogo);
        remoteViews.setImageViewResource(R.id.notificationOrder_backIcon, R.drawable.notify_order);
        int getCount = msg.length();
        if (getCount > 130){
            String mm = msg.substring(0, 130) + "...";
            remoteViews.setTextViewText(R.id.notificationOrder_message, mm);
        } else
            remoteViews.setTextViewText(R.id.notificationOrder_message, msg);

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        String getCurrentTime = currentTimeFormat.format(postTime.getTime());

        remoteViews.setTextViewText(R.id.notificationOrder_date, getCurrentTime);
        remoteViews.setTextViewText(R.id.notificationOrder_title, "Elinam Trendz");

        /* Invoking the default notification service */
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setTicker("New Order Alert!");
        mBuilder.setContentTitle("Elinam Trendz");
        mBuilder.setContentText(msg);
        mBuilder.setSmallIcon(R.drawable.notification_icon);
        mBuilder.setCustomBigContentView(remoteViews);
        mBuilder.setSound(defaultSound);
        mBuilder.setAutoCancel(true);


        /* Increase notification number every time a new notification arrives */
        mBuilder.setNumber(++numMessages);

        /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );
        mBuilder.setContentIntent(resultPendingIntent);
        //notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /* notificationID allows you to update the notification later on. */
        notificationManager.notify(notificationID, mBuilder.build());

    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!this.isRunning){
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }
}
