package com.tmsoft.tm.elitrends.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elitrends.Holders.LastMessageSendTimeClass;
import com.tmsoft.tm.elitrends.Holders.publicNotificationClass;
import com.tmsoft.tm.elitrends.R;
import com.tmsoft.tm.elitrends.Urls.SendEmail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PublicNotificationActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private EditText message;
    private ImageView send;
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private RelativeLayout noNotification;
    private SwipeRefreshLayout refresh;
    private ArrayList<String> keyList, allEmailAddress;

    private DatabaseReference databaseReference;

    private String getCurrentUserId;

    private NotificationManager mNotificationManager;
    private int notificationID = 100;
    private int numMessages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_notification);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("InnerNotification");
        databaseReference.keepSynced(true);

        myToolBar = (Toolbar) findViewById(R.id.publicNotification_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Notification");

        getCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        keyList = new ArrayList<>();
        allEmailAddress = new ArrayList<>();

        send = (ImageView) findViewById(R.id.publicNotification_sendText);
        message = (EditText) findViewById(R.id.publicNotification_enterMessage);
        progressDialog = new ProgressDialog(this);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        noNotification = (RelativeLayout) findViewById(R.id.publicNotification_noNotification);
        refresh = (SwipeRefreshLayout) findViewById(R.id.publicNotification_refresh);

        noNotification.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.publicNotification_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = message.getText().toString();

                if(TextUtils.isEmpty(msg))
                    Toast.makeText(PublicNotificationActivity.this, "Enter message", Toast.LENGTH_SHORT).show();
                else
                    showQuestionDialog(msg);
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try{
                    displayAllNotification();
                    checkNotification();
                } catch (Exception e){

                }
                refresh.setRefreshing(false);
            }
        });

        try{
            displayAllNotification();
            checkNotification();
        } catch (Exception e){

        }
    }

    private void checkNotification() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int num = (int) dataSnapshot.getChildrenCount();

                    if (num == 0)
                        noNotification.setVisibility(View.VISIBLE);
                    else
                        noNotification.setVisibility(View.GONE);
                } else
                    noNotification.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void displayAllNotification() {
        DatabaseReference dRef = databaseReference.child(getCurrentUserId);
        FirebaseRecyclerAdapter<publicNotificationClass, PublicNotificationViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<publicNotificationClass, PublicNotificationViewHolder>(
                        publicNotificationClass.class,
                        R.layout.layout_public_notification,
                        PublicNotificationViewHolder.class,
                        dRef
                ) {
            @Override
            protected void populateViewHolder(final PublicNotificationViewHolder viewHolder, final publicNotificationClass model, int position) {
                final String title = model.getTitle();
                final String userId = model.getUserId();
                final String activityToGo = model.getActivityIoGo();
                final String postKey = model.getPostKey();
                final String key = getRef(position).getKey();
                viewHolder.setRead(model.getRead());
                viewHolder.setMessage(model.getMessage(), false);
                viewHolder.setTime(model.getTime(), getApplicationContext());
                viewHolder.setMessageTitle(model.getMessageTitle());
                viewHolder.setTitle(model.getTitle());

                new Thread(){
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(model.getMessage()) && TextUtils.isEmpty(model.getMessageTitle())) {
                            DatabaseReference datReference = FirebaseDatabase.getInstance().getReference().child("InnerNotification").child(getCurrentUserId);
                            datReference.child(key).removeValue();
                        }
                    }
                }.start();

                if (TextUtils.isEmpty(title) || title.equalsIgnoreCase("delete")){
                    viewHolder.setProfilePicture("");
                    viewHolder.setMessage(model.getMessage(), true);
                } else {
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                    userReference.keepSynced(true);
                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                if (dataSnapshot.hasChild("profilePicture")){
                                    String pProfilePicture = dataSnapshot.child("profilePicture").getValue().toString();
                                    viewHolder.setProfilePicture(pProfilePicture);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isOnline()){
                            progressDialog.setMessage("Loading page...");
                            progressDialog.setCanceledOnTouchOutside(true);
                            progressDialog.show();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("InnerNotification").child(getCurrentUserId).child(key);
                            ref.child("read").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        if (title.equalsIgnoreCase("delete")){
                                            showAlertDialog(model.getMessage(), model.getMessageTitle());
                                            progressDialog.dismiss();
                                        } else {
                                            userClickNotification(title, userId, activityToGo, postKey);
                                            progressDialog.dismiss();
                                        }
                                    } else{
                                        Toast.makeText(PublicNotificationActivity.this, "Please report this", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            Snackbar snackbar = Snackbar.make(view, "No internet connection and may delay performance", Snackbar.LENGTH_LONG)
                                    .setAction("Continue", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (title.equalsIgnoreCase("delete"))
                                                showAlertDialog(model.getMessage(), model.getMessageTitle());
                                            else
                                                userClickNotification(title, userId, activityToGo, postKey);
                                        }
                                    });
                            snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                            snackbar.show();
                        }
                    }
                });

                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.setMessage("Deleting...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users");
                        userReference.keepSynced(true);
                        userReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        String shot = snapshot.child("userId").getValue().toString();
                                        DatabaseReference hh = FirebaseDatabase.getInstance().getReference().child("InnerNotification");
                                        hh.child(shot).child(key).removeValue();
                                        Log.i("key", key);
                                    }
                                    Toast.makeText(PublicNotificationActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void showAlertDialog(String message, String title) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PublicNotificationActivity.this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Home Page", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendUserToActivity("postKey", "mainNotification", MainActivity.class);
            }
        }).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void userClickNotification(String title, String userId, String activityToGo, String postKey) {
        if (title.equalsIgnoreCase("order")){
            if (!TextUtils.isEmpty(activityToGo) && !TextUtils.isEmpty(postKey) && !TextUtils.isEmpty(userId)){
                if (activityToGo.equalsIgnoreCase("BuyForMeActivity"))
                    sendUserToActivity("postKey", postKey, BuyForMeActivity.class);
                else if (activityToGo.equalsIgnoreCase("ViewProductOrderDetailsActivity"))
                    sendUserToActivity("productOrderKey", postKey, ViewProductOrderDetailsActivity.class);
                else if (activityToGo.equalsIgnoreCase("ShowCartViewOrderDetailsActivity")){
                    Intent intent = new Intent(PublicNotificationActivity.this, ShowCartViewOrderDetailsActivity.class);
                    intent.putExtra("cartPostKey", postKey);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            } else if (!TextUtils.isEmpty(activityToGo) && TextUtils.isEmpty(postKey)){
                if (activityToGo.equalsIgnoreCase("BuyForMeActivity"))
                    sendUserToActivity("postKey", "order", AllOrdersActivity.class);
                else if (activityToGo.equalsIgnoreCase("ViewProductOrderDetailsActivity"))
                    sendUserToActivity("postKey", "order", AllOrdersActivity.class);
                else if (activityToGo.equalsIgnoreCase("ShowCartViewOrderDetailsActivity"))
                    sendUserToActivity("postKey", "order", AllOrdersActivity.class);
            } else
                Toast.makeText(PublicNotificationActivity.this, "Please report this", Toast.LENGTH_LONG).show();
        } else if (title.equalsIgnoreCase("chat")){
            if (!TextUtils.isEmpty(userId))
                sendUserToActivity("userId", userId, ChatActivity.class);
            else
                Toast.makeText(PublicNotificationActivity.this, "Please report this", Toast.LENGTH_LONG).show();
        } else if (title.equalsIgnoreCase("comment")){
            if (!TextUtils.isEmpty(postKey))
                sendUserToActivity("postKey", postKey, ViewProductDetailsActivity.class);
            else
                Toast.makeText(PublicNotificationActivity.this, "Please report this", Toast.LENGTH_LONG).show();
        }  else
            sendUserToActivity("postKey", "mainNotification", MainActivity.class);
    }

    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void sendUserToActivity(String name, String postKey, Class classToGo){
        Intent intent = new Intent(PublicNotificationActivity.this, classToGo);
        intent.putExtra(name, postKey);
        startActivity(intent);
    }

    private void showDeleteDialog() {
        TextView question;
        Button yes, no;

        dialog.setContentView(R.layout.dialog_question);
        question = dialog.findViewById(R.id.dialogQuestion_message);
        yes = dialog.findViewById(R.id.dialogQuestion_yes);
        no = dialog.findViewById(R.id.dialogQuestion_no);

        question.setText("Are you sure you want to delete this notification");

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static class PublicNotificationViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView delete;

        public PublicNotificationViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            delete = mView.findViewById(R.id.layoutNotification_delete);
            delete.setVisibility(View.GONE);
        }

        public void setMessageTitle(String messageTitle) {
            TextView tt = mView.findViewById(R.id.layoutNotification_messageTitle);
            tt.setText(messageTitle);
        }

        public void setTime(long time, Context context) {
            TextView tt = mView.findViewById(R.id.layoutNotification_time);
            String lastSeenDisplayTime = LastMessageSendTimeClass.getLastTimeAgo(time, context);
            tt.setText(lastSeenDisplayTime);
        }

        public void setMessage(String message, boolean publicNotification) {
            TextView msg = mView.findViewById(R.id.layoutNotification_message);
            if (publicNotification){
                int len = message.length();
                if (len >= 47){
                    String newMsg = message.substring(0,42) + "...";
                    msg.setText(newMsg);
                } else
                    msg.setText(message);
            } else
                msg.setText(message);
        }

        public void setRead(String read) {
            LinearLayout linearLayout = mView.findViewById(R.id.layoutNotification_linear);
            if (read.equalsIgnoreCase("no"))
                linearLayout.setBackgroundResource(R.color.unread);
            else
                linearLayout.setBackgroundResource(R.color.white);
        }

        public void setTitle(String title) {
            try{
                if (title.equalsIgnoreCase("delete"))
                    delete.setVisibility(View.GONE);
                else
                    delete.setVisibility(View.GONE);
            } catch (Exception e){

            }
        }

        public void setProfilePicture(final String profilePic){
            final ImageView pp = mView.findViewById(R.id.layoutNotification_picture);
            try{
                if (profilePic.equalsIgnoreCase("")){
                    pp.setImageResource(R.drawable.notify_public_notification);
                } else {
                    //loading picture offline
                    Picasso.get()
                            .load(profilePic).fit()
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.warning)
                            .into(pp, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get()
                                            .load(profilePic).fit()
                                            .placeholder(R.drawable.warning)
                                            .into(pp);
                                }
                            });
                }
            } catch (Exception e){
                pp.setVisibility(View.GONE);
            }
        }
    }

    private void saveMessage(String msg) {
        progressDialog.setMessage("Publishing Notification...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        keyList.clear();
        getAllUserId(msg);
    }

    private long NUMBER_OF_USERS = 0;
    private void getAllUserId(final String msg) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userReference.keepSynced(true);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    NUMBER_OF_USERS = dataSnapshot.getChildrenCount();
                    int num = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String postKey = snapshot.child("userId").getValue().toString();
                        allEmailAddress.add(snapshot.child("email").getValue().toString());
                        ++num;
                        if (NUMBER_OF_USERS == num){
                            try {
                                sendEmailToAllClient("Elinam Trendz - General Notification", msg);
                            } catch (Exception e){
                                Log.i("error", e.getMessage());
                            }
                        }
                        Log.i("key", postKey);
                        saveNotification(postKey, msg);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveNotification(String key, final String msg){
        Map saveMap = new HashMap();
        saveMap.put("time", ServerValue.TIMESTAMP);
        saveMap.put("messageTitle", "General Notification");
        saveMap.put("message", msg);
        saveMap.put("title", "delete");
        saveMap.put("activityIoGo", "MainActivity");
        saveMap.put("read", "no");
        saveMap.put("userId", key);

        databaseReference.child(key).push().setValue(saveMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // notifyUser(msg);
                    dialog.dismiss();
                    Toast.makeText(PublicNotificationActivity.this, "Notification Published Successfully...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    message.setText("");
                } else {
                    String err = task.getException().getMessage();
                    Toast.makeText(PublicNotificationActivity.this, err, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void sendEmailToAllClient(String title, String msg) {
        String[] emailAddress = new String[allEmailAddress.size()];
        for (int x = 0; x < emailAddress.length; ++x){
            emailAddress[x] = allEmailAddress.get(x);
            Log.i("email", emailAddress[x]);
        }
        SendEmail email = new SendEmail(emailAddress, title, msg, PublicNotificationActivity.this, recyclerView);
        email.sendEmail();
    }

    /*private void notifyUser(String msg) {
        Log.i("Start", "notification");

        //setting the notification sound
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        *//* Invoking the default notification service *//*
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(PublicNotificationActivity.this);
        mBuilder.setContentTitle("Elinam Trendz");
        mBuilder.setContentText(msg);
        mBuilder.setTicker("New Message Alert!");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setSound(defaultSound);
        mBuilder.setAutoCancel(true);

        *//* Increase notification number every time a new notification arrives *//*
        mBuilder.setNumber(++numMessages);

        *//* Creates an explicit intent for an Activity in your app *//*
        Intent resultIntent = new Intent(PublicNotificationActivity.this, PublicNotificationActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(PublicNotificationActivity.this);
        stackBuilder.addParentStack(PublicNotificationActivity.class);

        *//* Adds the Intent that starts the Activity to the top of the stack *//*
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        *//* notificationID allows you to update the notification later on. *//*
        mNotificationManager.notify(notificationID, mBuilder.build());
    }*/

    private void showQuestionDialog(final String input){
        TextView question;
        Button yes, no;

        dialog.setContentView(R.layout.dialog_question);
        question = dialog.findViewById(R.id.dialogQuestion_message);
        yes = dialog.findViewById(R.id.dialogQuestion_yes);
        no = dialog.findViewById(R.id.dialogQuestion_no);

        question.setText("Are you sure you want to publish this notification");

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMessage(input);
            }
        });

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
