package com.tmsoft.tm.elitrends.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elitrends.Holders.LastMessageSendTimeClass;
import com.tmsoft.tm.elitrends.Holders.chatUserNotify;
import com.tmsoft.tm.elitrends.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMembersActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;

    private String getCurrentId;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_members);

        myToolBar = findViewById(R.id.chatMember_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myToolBar.setTitle("Chat Center");

        refresh = (SwipeRefreshLayout) findViewById(R.id.chatMember_refresh);

        getCurrentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ServerChat");
        databaseReference.keepSynced(true);

        String getCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference().child("AllDeviceToken").child(getCurrentUserId);
        tokenRef.keepSynced(true);
        tokenRef.child("chat").setValue("yes");

        recyclerView = findViewById(R.id.chatMember_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayMember();
                refresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        displayMember();
    }

    private void displayMember() {
        Query query = databaseReference.orderByChild("counter");

        FirebaseRecyclerAdapter<chatUserNotify, usersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<chatUserNotify, usersViewHolder>(

                        chatUserNotify.class,
                        R.layout.layout_chat_notify,
                        usersViewHolder.class,
                        query

                ) {
                    @Override
                    protected void populateViewHolder(final usersViewHolder viewHolder, chatUserNotify model, int position) {
                        final String senderId = model.getFrom();
                        final String key = getRef(position).getKey();
                        viewHolder.setTime(model.getTime());
                        viewHolder.setMessage(model.getMessage());
                        viewHolder.setRead(model.getRead());

                       try{
                           DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Users")
                                   .child(senderId);
                           dataRef.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   if (dataSnapshot.exists()){
                                       if (dataSnapshot.hasChild("profilePicture")){
                                           String pP = dataSnapshot.child("profilePicture").getValue().toString();
                                           viewHolder.setProfilePicture(pP, ChatMembersActivity.this);
                                       }

                                       if (dataSnapshot.hasChild("fullName")){
                                           String fN = dataSnapshot.child("fullName").getValue().toString();
                                           viewHolder.setName(fN);
                                       }
                                   }
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {

                               }
                           });
                       } catch (Exception e){

                       }

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {
                                databaseReference.child(key).child("read").setValue("yes");
                                sendUserToMainChatActivity(senderId);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class usersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        CircleImageView profileImage;
        TextView msg, mtime, name;
        ImageView newIcon;

        public usersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            profileImage = mView.findViewById(R.id.chat_ProfilePicture);
            name = mView.findViewById(R.id.chat_UserName);
            mtime = mView.findViewById(R.id.chat_TimeTime);
            msg = mView.findViewById(R.id.chat_message);
            newIcon = mView.findViewById(R.id.chat_newIcon);
        }

        public void setProfilePicture(final String profilePicture, final Context context) {
            //loading picture offline
            Picasso.get()
                    .load(profilePicture).fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.profile_image).into(profileImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get()
                            .load(profilePicture).fit()
                            .placeholder(R.drawable.profile_image).into(profileImage);
                }
            });
        }

        public  void setName(String nam){
            name.setText(nam);
        }

        public void setMessage(String message) {
            String realMsg = message;
            try{
                if (realMsg.length() >= 27){
                    String eMsg = realMsg.substring(0,24);
                    String ps = eMsg + "***";

                    msg.setText(ps);
                } else{
                    msg.setText(message);
                }
            } catch (Exception ignored){

            }
        }

        public void setTime(long time) {
            LastMessageSendTimeClass lastSeenTimeClass = new LastMessageSendTimeClass();

            try {
                String lastSeenDisplayTime = LastMessageSendTimeClass.getLastTimeAgo(time, mView.getContext());
                mtime.setText(lastSeenDisplayTime);
            } catch (Exception ex){

            }
        }

        @SuppressLint("ResourceAsColor")
        public void setRead(String read){
            if (read.equals("no")){
                newIcon.setVisibility(View.VISIBLE);
            }

            if (read.equals("yes")) {
                newIcon.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToMainChatActivity(String senderId) {
        Intent intent = new Intent(ChatMembersActivity.this, ChatActivity.class);
        intent.putExtra("userId", senderId);
        startActivity(intent);
    }
}
