package com.tmsoft.tm.elitrends.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elitrends.Holders.commentElement;
import com.tmsoft.tm.elitrends.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView commentRecyclerView;
    private EditText commentText;
    private ImageButton commentButton;
    private ProgressDialog progressDialog;
    private RelativeLayout noComment;
    private SwipeRefreshLayout refresh;

    private DatabaseReference commentReference, commentView, userPostReference;
    private String getCurrentUserId;
    private FirebaseAuth mAuth;

    private String postKey, commentFullName, commentProfilePicture, commentDate, commentTime, commentRandomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        commentReference = FirebaseDatabase.getInstance().getReference().child("Comments");
        commentReference.keepSynced(true);
        userPostReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userPostReference.keepSynced(true);

        postKey = getIntent().getExtras().get("postKey").toString();

        commentButton = (ImageButton) findViewById(R.id.comment_commentButton);
        commentText = (EditText) findViewById(R.id.comment_commentTextBox);
        progressDialog = new ProgressDialog(this);
        noComment = (RelativeLayout) findViewById(R.id.comment_noComment);
        refresh = (SwipeRefreshLayout) findViewById(R.id.comment_refresh);

        noComment.setVisibility(View.GONE);

        myToolBar = (Toolbar) findViewById(R.id.comment_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Comments");

        commentRecyclerView = (RecyclerView) findViewById(R.id.comment_recyclerView);
        commentRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentRecyclerView.setLayoutManager(linearLayoutManager);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayAllPostComment(postKey);
                checkComments(postKey);
                refresh.setRefreshing(false);
            }
        });

        //displaying all post comment
        displayAllPostComment(postKey);
        checkComments(postKey);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comments = commentText.getText().toString();

                if(TextUtils.isEmpty(comments)) {
                    Toast.makeText(CommentActivity.this, "Please enter your comment", Toast.LENGTH_SHORT).show();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
                else {
                    addUserComment(postKey, comments);
                }
            }
        });
    }

    private void checkComments(String postKey) {
        commentView = FirebaseDatabase.getInstance().getReference().child("Comments").child(postKey);
        commentView.keepSynced(true);
        commentView.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int num = (int) dataSnapshot.getChildrenCount();

                    if (num == 0)
                        noComment.setVisibility(View.VISIBLE);
                    else
                        noComment.setVisibility(View.GONE);
                } else
                    noComment.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //comment adding
    private void addUserComment(final String postKey, final String comments) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        progressDialog.setTitle("Uploading Comment");
        progressDialog.setMessage("Please wait patiently while your comment is uploaded");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        userPostReference.child(getCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("fullName")){
                        commentFullName = dataSnapshot.child("fullName").getValue().toString();
                    }
                    if(dataSnapshot.hasChild("profilePicture")){
                        commentProfilePicture = dataSnapshot.child("profilePicture").getValue().toString();
                    }

                    Calendar date = Calendar.getInstance();
                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
                    commentDate = currentDateFormat.format(date.getTime());

                    Calendar time = Calendar.getInstance();
                    SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
                    commentTime = currentTimeFormat.format(time.getTime());

                    commentRandomName = postKey + System.currentTimeMillis();

                    HashMap<String, String> commentMap = new HashMap<>();
                    commentMap.put("commentUserId", getCurrentUserId);
                    commentMap.put("commentUsername", commentFullName);
                    commentMap.put("commentProfilePicture", commentProfilePicture);
                    commentMap.put("commentDate", commentDate);
                    commentMap.put("commentTime", commentTime);
                    commentMap.put("comment", comments);

                    commentReference.child(postKey).push().setValue(commentMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(CommentActivity.this, "Comment uploaded successfully", Toast.LENGTH_SHORT).show();
                                        commentText.setText("");
                                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                                        progressDialog.dismiss();
                                    } else {
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(CommentActivity.this, "Error Occurred... " + errorMessage
                                                + "\nPlease try again", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //displaying all post comment
    private void displayAllPostComment(String postKey) {
        commentView = FirebaseDatabase.getInstance().getReference().child("Comments").child(postKey);
        commentView.keepSynced(true);

        FirebaseRecyclerAdapter<commentElement, commentViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<commentElement, commentViewHolder>(
                        commentElement.class,
                        R.layout.layout_comment_view,
                        commentViewHolder.class,
                        commentView

                ) {
                    @Override
                    protected void populateViewHolder(commentViewHolder viewHolder, commentElement model, int position) {
                        final String userId = model.getCommentUserId();

                        viewHolder.setComment(model.getComment());
                        viewHolder.setCommentDate(model.getCommentDate());
                        viewHolder.setCommentTime(model.getCommentTime());
                        viewHolder.setCommentProfilePicture(getApplicationContext(), model.getCommentProfilePicture());
                        viewHolder.setCommentUsername(model.getCommentUsername());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(CommentActivity.this, ViewUserProfileActivity.class);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                    }
                };
        commentRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class commentViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public commentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCommentUsername(String commentUsername) {
            TextView userName = mView.findViewById(R.id.commentUserName);
            userName.setText(commentUsername);
        }

        public void setCommentProfilePicture(final Context context, final String commentProfilePicture) {
            final CircleImageView profilePicture = mView.findViewById(R.id.commentProfilePicture);
            try{
                //Picasso.with(context).load(commentProfilePicture).into(profilePicture);
                //loading picture offline
                Picasso.get()
                        .load(commentProfilePicture).fit()
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.profile_image)
                        .into(profilePicture, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(commentProfilePicture).fit()
                                .placeholder(R.drawable.profile_image)
                                .into(profilePicture);
                    }
                });
            } catch (Exception ex){

            }
        }

        public void setCommentDate(String commentDate) {
            TextView commentDateSet = mView.findViewById(R.id.commentDateDate);
            commentDateSet.setText(commentDate);
        }

        public void setCommentTime(String commentTime) {
            TextView commentTimeSet = mView.findViewById(R.id.commentTimeTime);
            commentTimeSet.setText(commentTime);
        }

        public void setComment(String comment) {
            TextView commentComent = mView.findViewById(R.id.realComment);
            commentComent.setText(comment);
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
}
