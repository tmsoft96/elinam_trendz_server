package com.tmsoft.tm.elitrends.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elitrends.Adapters.MessageAdapter;
import com.tmsoft.tm.elitrends.Holders.MessagesClass;
import com.tmsoft.tm.elitrends.Message.AllMessage;
import com.tmsoft.tm.elitrends.R;
import com.tmsoft.tm.elitrends.Urls.InnerNotification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {

    private EditText writeMessage;
    private ImageButton button;
    private RecyclerView recyclerView;
    private Toolbar myToolBar;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private String senderId;
    private DatabaseReference rootReference, notificationReference, onlineReference;
    private InnerNotification innerNotification;

    private final List<MessagesClass> messagesClassList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    private String receiverId;
    private String notifySender1, notifySender2, notifySender3;
    private String actualSenderId;
    private String clientStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //sender's id is tmsoft
        senderId = "tmsoft";

        writeMessage = (EditText) findViewById(R.id.chat_writeMessage);
        button = (ImageButton) findViewById(R.id.chat_sendMessageButton);

        myToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chat");

        mAuth = FirebaseAuth.getInstance();
        actualSenderId = mAuth.getCurrentUser().getUid();
        rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference.keepSynced(true);

        receiverId = getIntent().getExtras().get("userId").toString();


        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationReference.keepSynced(true);

        onlineReference = FirebaseDatabase.getInstance().getReference().child("OnlineUser");
        onlineReference.keepSynced(true);

        messageAdapter = new MessageAdapter(messagesClassList, getApplicationContext());

        progressDialog = new ProgressDialog(this);

        recyclerView = (RecyclerView) findViewById(R.id.chat_recycler);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        recyclerView.setAdapter(messageAdapter);

        DatabaseReference notifySender = FirebaseDatabase.getInstance().getReference().child("ServerId");
        notifySender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("0")){
                        notifySender1 = dataSnapshot.child("0").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("1")){
                        notifySender2 = dataSnapshot.child("1").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("2")){
                        notifySender3 = dataSnapshot.child("2").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        saveOnlineUser("online");
        collectClientOnlineStatus();
        ShowMessages();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = writeMessage.getText().toString();

                if (TextUtils.isEmpty(msg))
                    Toast.makeText(ChatActivity.this, "Message box empty", Toast.LENGTH_SHORT).show();
                else {
                    sendUserMessage(msg);
                    writeMessage.setText("");
                }
            }
        });
    }

    private void saveOnlineUser(String status) {
        onlineReference.child("serverStatus").setValue(status)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                        } else {
                            String err = task.getException().getMessage();
                            Toast.makeText(ChatActivity.this, err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void ShowMessages(){
        rootReference.child("Messages").child(senderId).child(receiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        MessagesClass messages = dataSnapshot.getValue(MessagesClass.class);
                        messagesClassList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void collectClientOnlineStatus() {
        onlineReference.child("clientStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(receiverId)){
                        clientStatus = dataSnapshot.child(receiverId).getValue().toString();
                    } else {
                        clientStatus = "offline";
                    }
                } else {
                    clientStatus = "offline";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendUserMessage(final String msg) {
        progressDialog.setMessage("sending...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();
        String msgSenderRef = "Messages/" + senderId + "/" + receiverId;
        String msgReceiverRef = "Messages/" + receiverId + "/" + senderId;

        DatabaseReference msgKeyRef = rootReference.child("Messages").child(senderId).child(receiverId).push();
        String msgKey = msgKeyRef.getKey();

        Map messageMap = new HashMap();
        messageMap.put("message", msg);
        messageMap.put("type", "text");
        messageMap.put("time", ServerValue.TIMESTAMP);
        messageMap.put("from", senderId);
        messageMap.put("actualSenderId", actualSenderId);

        Map messageDetailsMap = new HashMap();
        messageDetailsMap.put(msgSenderRef + "/" + msgKey, messageMap);
        messageDetailsMap.put(msgReceiverRef + "/" + msgKey, messageMap);

        rootReference.updateChildren(messageDetailsMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){

                }

                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);

                final String tt = "Elinam Trendz, message";

                //sending notification
                final HashMap<String, String> notificationData = new HashMap<>();
                notificationData.put("from", actualSenderId);
                notificationData.put("title", tt);
                notificationData.put("message", msg);

                if (!clientStatus.equals("online")){
                    notificationReference.child(receiverId).push().setValue(notificationData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        innerNotification = new InnerNotification(
                                                "chat",
                                                "no",
                                                actualSenderId,
                                                "ChatActivity",
                                                ServerValue.TIMESTAMP.toString(),
                                                msg,
                                                tt,
                                                receiverId,
                                                ""
                                        );
                                        boolean deter = innerNotification.onSaveAll();
                                        if (deter){
                                            if (!TextUtils.isEmpty(notifySender1) && !notifySender1.equals(actualSenderId)){
                                                notificationReference.child(notifySender1).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }

                                            if (!TextUtils.isEmpty(notifySender2) && !notifySender2.equals(actualSenderId)){
                                                notificationReference.child(notifySender2).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }

                                            if (!TextUtils.isEmpty(notifySender3) && !notifySender3.equals(actualSenderId)){
                                                notificationReference.child(notifySender3).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }
                                            progressDialog.dismiss();
                                            writeMessage.setText("");
                                        } else {
                                            if (!TextUtils.isEmpty(notifySender1) && !notifySender1.equals(actualSenderId)){
                                                notificationReference.child(notifySender1).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }

                                            if (!TextUtils.isEmpty(notifySender2) && !notifySender2.equals(actualSenderId)){
                                                notificationReference.child(notifySender2).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }

                                            if (!TextUtils.isEmpty(notifySender3) && !notifySender3.equals(actualSenderId)){
                                                notificationReference.child(notifySender3).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }
                                            progressDialog.dismiss();
                                            writeMessage.setText("");
                                        }
                                    } else {
                                        String er = task.getException().getMessage();
                                        progressDialog.dismiss();
                                        Toast.makeText(ChatActivity.this, er, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    progressDialog.dismiss();
                    writeMessage.setText("");
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            saveOnlineUser("offline");
            finish();
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            saveOnlineUser("offline");
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
