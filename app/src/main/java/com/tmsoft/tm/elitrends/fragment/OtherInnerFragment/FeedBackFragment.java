package com.tmsoft.tm.elitrends.fragment.OtherInnerFragment;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.tmsoft.tm.elitrends.Holders.feedbackClass;
import com.tmsoft.tm.elitrends.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedBackFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private RelativeLayout noFeedback;
    private SwipeRefreshLayout refresh;

    private DatabaseReference databaseReference, userReference;

    private String pP, fN, pN;

    public FeedBackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_feed_back, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Feedback");
        databaseReference.keepSynced(true);
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userReference.keepSynced(true);

        noFeedback = view.findViewById(R.id.fragmentFeedback_noFeedback);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.fragmentFeedback_refresh);

        recyclerView = view.findViewById(R.id.fragmentFeedback_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);

        noFeedback.setVisibility(View.GONE);

        checkFeedback();
        showAllFeedback();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkFeedback();
                showAllFeedback();
                refresh.setRefreshing(false);
            }
        });

        return view;
    }

    private void checkFeedback() {
        Query query = databaseReference.orderByChild("number");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int num = (int) dataSnapshot.getChildrenCount();

                    if (num == 0)
                        noFeedback.setVisibility(View.VISIBLE);
                    else
                        noFeedback.setVisibility(View.GONE);
                } else
                    noFeedback.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showAllFeedback() {
        Query query = databaseReference.orderByChild("number");

        FirebaseRecyclerAdapter<feedbackClass, feedbackViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<feedbackClass, feedbackViewHolder>(
                feedbackClass.class,
                R.layout.layout_feedback,
                feedbackViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(final feedbackViewHolder viewHolder, final feedbackClass model, int position) {
                String userId = model.getUserId();

                viewHolder.setFeedback(model.getFeedback());
                viewHolder.setTime(model.getTime(), getContext());

                userReference.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            if (dataSnapshot.hasChild("profilePicture")) {
                                pP = dataSnapshot.child("profilePicture").getValue().toString();
                                viewHolder.setProfilePicture(pP, getContext());
                            }

                            if (dataSnapshot.hasChild("fullName")){
                                fN = dataSnapshot.child("fullName").getValue().toString();
                                viewHolder.setUserName(fN);
                            }

                            if (dataSnapshot.hasChild("phoneNumber")){
                                pN = dataSnapshot.child("phoneNumber").getValue().toString();
                                viewHolder.setPhoneNumber(pN);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        final Dialog dialog = new Dialog(view.getContext(), R.style.Theme_CustomDialog);
                        TextView close, name, number, feedback, date;
                        final CircleImageView profilePicture;

                        dialog.setContentView(R.layout.dialog_feedback);
                        close = dialog.findViewById(R.id.dialogFeedback_close);
                        name = dialog.findViewById(R.id.dialogFeedback_username);
                        number = dialog.findViewById(R.id.dialogFeedback_phoneNumber);
                        feedback = dialog.findViewById(R.id.dialogFeedback_msg);
                        date = dialog.findViewById(R.id.dialogFeedback_date);
                        profilePicture = dialog.findViewById(R.id.dialogFeedback_profilePicture);

                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        name.setText(fN);
                        number.setText(pN);
                        feedback.setText(model.getFeedback());

                        LastMessageSendTimeClass lastMessageSendTimeClass = new LastMessageSendTimeClass();
                        String getTime = LastMessageSendTimeClass.getLastTimeAgo(model.getTime(), view.getContext());
                        date.setText(getTime);

                        //loading picture offline
                        Picasso.get()
                                .load(pP).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.profile_image).into(profilePicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(pP).fit()
                                        .placeholder(R.drawable.profile_image).into(profilePicture);
                            }
                        });

                        dialog.show();
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class feedbackViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public feedbackViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFeedback(String feedback) {
            TextView mFeedbackMessage = mView.findViewById(R.id.feedback_message);
            //67
            int fnum = feedback.length();
            if (fnum >= 67){
                String fm = feedback.substring(0, 63);
                String ff = fm + "***";
                mFeedbackMessage.setText(ff);
            } else
                mFeedbackMessage.setText(feedback);
        }

        public void setTime(long time, Context context) {
            LastMessageSendTimeClass lastMessageSendTimeClass = new LastMessageSendTimeClass();
            String getTime = LastMessageSendTimeClass.getLastTimeAgo(time, context);

            TextView mTime = mView.findViewById(R.id.feedbackDate);
            mTime.setText(getTime);
        }

        public void setProfilePicture (final String profilePicture, final Context context){
            final CircleImageView mProfilePicture = mView.findViewById(R.id.feedback_profilePicture);
            //loading picture offline
            Picasso.get()
                    .load(profilePicture).fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.profile_image).into(mProfilePicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get()
                            .load(profilePicture).fit()
                            .placeholder(R.drawable.profile_image).into(mProfilePicture);
                }
            });
        }

        public void setUserName (String userName){
            TextView mUsername = mView.findViewById(R.id.feedback_userName);
            mUsername.setText(userName);
        }

        public void setPhoneNumber (String phoneNumber){
            TextView mPhoneNumber = mView.findViewById(R.id.feedback_phoneNumber);
            mPhoneNumber.setText(phoneNumber);
        }
    }

}
